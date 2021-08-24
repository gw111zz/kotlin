/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.pm20

import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.gradle.plugin.mpp.ChooseVisibleSourceSetsImpl
import org.jetbrains.kotlin.gradle.plugin.mpp.MetadataDependencyResolution
import org.jetbrains.kotlin.gradle.plugin.mpp.getMetadataExtractor
import org.jetbrains.kotlin.gradle.utils.getOrPutRootProjectProperty
import org.jetbrains.kotlin.project.model.*
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap

/**
 * Why we need it?
 *
 * 1. Need case: having some dependency, fragments, variants in terms KPM, we want to get files corresponding to them
 *    See MetadataExtractor as Gradle implementation of getting the files
 *
 * 2. Drives the DefaultModuleDependencyExpander
 *    ModuleDependencyExpander just expands given dependencies without much thought. We need some thing which would
 *    read actual dependencies list and will call Expander
 *
 */
internal class FragmentGranularMetadataResolver(private val ownerModule: KotlinGradleModule) {
    private val project: Project
        get() = ownerModule.project

    private val resolutionsCache: MutableMap<KotlinModuleFragment, Collection<MetadataDependencyResolution>> = ConcurrentHashMap()

    private val moduleResolver = GradleModuleDependencyResolver.getForCurrentBuild(project)
    private val variantResolver = GradleModuleVariantResolver.getForCurrentBuild(project)
    private val dependencyExpander = DefaultModuleDependencyExpander(variantResolver)
    private val dependencyGraphResolver = GradleKotlinDependencyGraphResolver(moduleResolver)

    fun getMetadataDependenciesForFragment(requestingFragment: KotlinModuleFragment): Iterable<MetadataDependencyResolution> {
        return resolutionsCache.getOrPut(requestingFragment) {
            doResolveMetadataDependenciesForFragment(requestingFragment)
        }
    }

    @TestOnly /* called in tests via injecting scripts */
    fun renderDependenciesTransformations(): String {
        val fragmentsToResolutions = ownerModule.fragments.associateWith { getMetadataDependenciesForFragment(it) }

        return buildString {
            for ((fragment, resolutions) in fragmentsToResolutions) {
                appendLine("Fragment $fragment of module $ownerModule")
                resolutions.forEach {
                    appendLine("    $it")
                }
            }
        }
    }

    private fun doResolveMetadataDependenciesForFragment(requestingFragment: KotlinModuleFragment): Collection<MetadataDependencyResolution> {
        val configurationToResolve = configurationToResolveMetadataDependencies(project, requestingFragment.containingModule)
        val resolvedDependenciesByModuleId =
            configurationToResolve.incoming.resolutionResult.allDependencies.filterIsInstance<ResolvedDependencyResult>()
                .associateBy { it.toModuleIdentifier() }

        val dependencyGraph = dependencyGraphResolver.resolveDependencyGraph(requestingFragment.containingModule)

        if (dependencyGraph is DependencyGraphResolution.Unknown)
            error("unexpected failure in dependency graph resolution for $requestingFragment in $project")

        dependencyGraph as GradleDependencyGraph // refactor the type hierarchy to avoid this downcast? FIXME?
        val fragmentsToInclude = requestingFragment.refinesClosure
        val requestedDependencies = dependencyGraph.root.dependenciesByFragment.filterKeys { it in fragmentsToInclude }.values.flatten()

        val visited = mutableSetOf<GradleDependencyGraphNode>()
        val fragmentResolutionQueue = ArrayDeque<GradleDependencyGraphNode>(requestedDependencies)

        val results = mutableSetOf<MetadataDependencyResolution>()

        while (fragmentResolutionQueue.isNotEmpty()) {
            val dependencyNode = fragmentResolutionQueue.removeFirst()
            visited.add(dependencyNode)

            val dependencyModule = dependencyNode.module

            val dependencyExpansionResult = dependencyExpander.expandModuleDependency(requestingFragment, dependencyModule)
            val chosenFragments = dependencyExpansionResult as? ModuleDependencyExpansionResult.ChosenFragments
            val visibleFragments = chosenFragments?.visibleFragments?.toList().orEmpty()

            val visibleTransitiveDependencies =
                dependencyNode.dependenciesByFragment.filterKeys { it in visibleFragments }.values.flattenTo(mutableSetOf())

            fragmentResolutionQueue.addAll(visibleTransitiveDependencies.filter { it !in visited })

            val resolvedDependencyResult = dependencyNode.gradleDependency
            val isResolvedAsProject = resolvedDependencyResult.toProjectOrNull(project)
            val result = when (dependencyModule) {
                is ExternalPlainKotlinModule -> {
                    MetadataDependencyResolution.KeepOriginalDependency(resolvedDependencyResult, isResolvedAsProject)
                }
                else -> run {
                    val metadataSourceDependency = dependencyNode.run { metadataDependency ?: gradleDependency }

                    val metadataExtractor = getMetadataExtractor(project, resolvedDependencyResult, configurationToResolve, true)

                    if (dependencyModule is ExternalImportedKotlinModule &&
                        metadataExtractor is JarArtifactMppDependencyMetadataExtractor &&
                        chosenFragments != null
                    ) {
                        resolveHostSpecificMetadataArtifacts(dependencyModule, chosenFragments, metadataExtractor)
                    }

                    val projectStructureMetadata = (dependencyModule as? ExternalImportedKotlinModule)?.projectStructureMetadata
                        ?: checkNotNull(metadataExtractor?.getProjectStructureMetadata())

                    val visibleFragmentNames = visibleFragments.map { it.fragmentName }.toSet()
                    val visibleFragmentNamesExcludingVisibleByParents =
                        visibleFragmentNames
                            .minus(requestingFragment.fragmentsNamesVisibleByParents())

                    ChooseVisibleSourceSetsImpl(
                        metadataSourceDependency,
                        isResolvedAsProject,
                        projectStructureMetadata,
                        visibleFragmentNames,
                        visibleFragmentNamesExcludingVisibleByParents,
                        visibleTransitiveDependencies.map { resolvedDependenciesByModuleId.getValue(it.module.moduleIdentifier) }.toSet(),
                        checkNotNull(metadataExtractor)
                    )
                }
            }
            results.add(result)
        }

        val resultSourceComponents = results.mapTo(mutableSetOf()) { it.dependency }
        resolvedDependenciesByModuleId.values.minus(resultSourceComponents).forEach {
            results.add(MetadataDependencyResolution.ExcludeAsUnrequested(it, it.toProjectOrNull(project)))
        }

        return results
    }

    private fun KotlinModuleFragment.fragmentsNamesVisibleByParents(): MutableSet<String> {
        val parentResolutionsForDependency = resolutionsCache[this].orEmpty()
        return parentResolutionsForDependency.filterIsInstance<ChooseVisibleSourceSetsImpl>()
            .flatMapTo(mutableSetOf()) { it.allVisibleSourceSetNames }
    }

    private fun resolveHostSpecificMetadataArtifacts(
        dependencyModule: ExternalImportedKotlinModule,
        chosenFragments: ModuleDependencyExpansionResult.ChosenFragments,
        metadataExtractor: JarArtifactMppDependencyMetadataExtractor
    ) {
        val visibleFragments = chosenFragments.visibleFragments
        val variantResolutions = chosenFragments.variantResolutions
        val hostSpecificFragments = dependencyModule.hostSpecificFragments
        val hostSpecificFragmentToArtifact = visibleFragments.intersect(hostSpecificFragments).mapNotNull { hostSpecificFragment ->
            val relevantVariantResolution = variantResolutions
                .filterIsInstance<VariantResolution.VariantMatch>()
                // find some of our variants that resolved a dependency's variant containing the fragment
                .find { hostSpecificFragment in it.chosenVariant.refinesClosure }
            // resolve the dependencies of that variant getting the host-specific metadata artifact
            relevantVariantResolution?.let { resolution ->
                val configurationResolvingPlatformVariant =
                    (resolution.requestingVariant as KotlinGradleVariant).compileDependencyConfiguration
                val hostSpecificArtifact = ResolvedMppVariantsProvider.get(project)
                    .getHostSpecificMetadataArtifactByRootModule(
                        dependencyModule.moduleIdentifier,
                        configurationResolvingPlatformVariant
                    )
                hostSpecificArtifact?.let { hostSpecificFragment.fragmentName to it }
            }
        }
        metadataExtractor.metadataArtifactBySourceSet.putAll(hostSpecificFragmentToArtifact)
    }

    companion object {
        fun getForModule(module: KotlinGradleModule): FragmentGranularMetadataResolver {
            val project = module.project
            val extraPropertyName = "org.jetbrains.kotlin.dependencyResolution.fragmentGranularMetadataResolvers.${project.getKotlinPluginVersion()}"
            val resolversCache = project.getOrPutRootProjectProperty(extraPropertyName) {
                mutableMapOf<KotlinGradleModule, FragmentGranularMetadataResolver>()
            }

            return resolversCache.getOrPut(module) { FragmentGranularMetadataResolver(module) }
        }
    }
}