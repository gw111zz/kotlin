/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.awaitPlatformTargets
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.categoryByName
import org.jetbrains.kotlin.gradle.plugin.hierarchy.KotlinSourceSetTreeClassifier
import org.jetbrains.kotlin.gradle.plugin.hierarchy.orNull
import org.jetbrains.kotlin.gradle.plugin.sources.*
import org.jetbrains.kotlin.gradle.plugin.sources.InternalKotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.sources.disambiguateName
import org.jetbrains.kotlin.gradle.plugin.usageByName
import org.jetbrains.kotlin.gradle.utils.*
import org.jetbrains.kotlin.gradle.utils.listProperty
import org.jetbrains.kotlin.gradle.utils.lowerCamelCaseName

/**
 * @see resolvableMetadataConfiguration
 */
internal val InternalKotlinSourceSet.resolvableMetadataConfigurationName: String
    get() = disambiguateName(lowerCamelCaseName("resolvable", METADATA_CONFIGURATION_NAME_SUFFIX))

/**
 * Represents a 'resolvable' configuration containing all dependencies in compile scope.
 * These dependencies are set up to resolve Kotlin Metadata (without transformation) and will resolve
 * consistently across the whole project.
 */
internal val InternalKotlinSourceSet.resolvableMetadataConfiguration: Configuration by extrasStoredProperty {
    assert(resolvableMetadataConfigurationName !in project.configurations.names)
    val configuration = project.configurations
        .maybeCreateResolvable(resolvableMetadataConfigurationName)
        .configureMetadataDependenciesAttribute(project)

    withDependsOnClosure.forAll { sourceSet ->
        val extenders = sourceSet.internal.compileDependenciesConfigurations
        configuration.extendsFrom(*extenders.toTypedArray())
    }

    /**
     * Adding dependencies from associate compilations using a listProvider, since we would like to defer
     * the call to 'getVisibleSourceSetsFromAssociateCompilations' as much as possible (changes to the model might significantly
     * change the result of this visible source sets)
     */
    configuration.dependencies.addAllLater(project.listProvider {
        getVisibleSourceSetsFromAssociateCompilations(this).flatMap { sourceSet ->
            sourceSet.internal.compileDependenciesConfigurations.flatMap { it.allDependencies }
        }
    })

    // needed for old IDEs
    configureLegacyMetadataDependenciesConfigurations(configuration)

    configuration
}

private val InternalKotlinSourceSet.compileDependenciesConfigurations: List<Configuration>
    get() = listOf(
        project.configurations.getByName(apiConfigurationName),
        project.configurations.getByName(implementationConfigurationName),
        project.configurations.getByName(compileOnlyConfigurationName),
    )

/**
Older IDEs still rely on resolving the metadata configurations explicitly.
Dependencies will be coming from extending the newer 'resolvableMetadataConfiguration'.

the intransitiveMetadataConfigurationName will not extend this mechanism, since it only
relies on dependencies being added explicitly by the Kotlin Gradle Plugin
 */
private fun InternalKotlinSourceSet.configureLegacyMetadataDependenciesConfigurations(resolvableMetadataConfiguration: Configuration) {
    @Suppress("DEPRECATION")
    listOf(
        apiMetadataConfigurationName,
        implementationMetadataConfigurationName,
        compileOnlyMetadataConfigurationName
    ).forEach { configurationName ->
        val configuration = project.configurations.getByName(configurationName)
        configuration.extendsFrom(resolvableMetadataConfiguration)
        configuration.shouldResolveConsistentlyWith(resolvableMetadataConfiguration)
    }
}

private fun Configuration.configureMetadataDependenciesAttribute(project: Project): Configuration = apply {
    usesPlatformOf(project.multiplatformExtension.metadata())
    attributes.setAttribute(Usage.USAGE_ATTRIBUTE, project.usageByName(KotlinUsages.KOTLIN_METADATA))
    attributes.setAttribute(Category.CATEGORY_ATTRIBUTE, project.categoryByName(Category.LIBRARY))
}

/**
 * Configuration containing all compile dependencies from *all* source sets.
 * This configuration is used to provide a dependency 'consistency scope' for
 * the [InternalKotlinSourceSet.resolvableMetadataConfiguration]
 */
private val Project.allCompileMetadataConfiguration
    get(): Configuration = configurations.findResolvable("allSourceSetsCompileDependenciesMetadata")
        ?: configurations
            .createResolvable("allSourceSetsCompileDependenciesMetadata")
            .configureMetadataDependenciesAttribute(project)

private inline fun <reified T> Project.listProvider(noinline provider: () -> List<T>): Provider<List<T>> {
    return project.objects.listProperty<T>().apply {
        set(project.provider(provider))
    }
}

/**
 * Ensure a consistent dependencies resolution result between Metadata Dependencies and Actual platform dependencies
 * TODO: explain why it is necessary
 */
internal val SetupConsistentMetadataDependenciesResolution = KotlinProjectSetupCoroutine {
    KotlinPluginLifecycle.Stage.AfterFinaliseRefinesEdges.await()

    val platformTargets = multiplatformExtension.awaitPlatformTargets()
    val mainSourceSets = platformTargets
        .mapNotNull { target -> target.compilations.firstOrNull { KotlinSourceSetTree.orNull(it) == KotlinSourceSetTree.main } }
        .flatMap { it.allKotlinSourceSets }
        .toSet()
    configureConsistentDependencyResolution(mainSourceSets, "allSourceSetsCompileDependenciesMetadata")

    val testSourceSets = platformTargets
        .mapNotNull { target -> target.compilations.firstOrNull { KotlinSourceSetTree.orNull(it) == KotlinSourceSetTree.test } }
        .flatMap { it.allKotlinSourceSets }
        .toSet()
    configureConsistentDependencyResolution(testSourceSets, "allTestSourceSetsCompileDependenciesMetadata")

//    val otherNonAndroidSourceSets = multiplatformExtension
//        .awaitSourceSets()
//        .filter { it.androidSourceSetInfoOrNull == null } // we don't care about android-specific source sets
//    otherNonAndroidSourceSets.groupBy

//    for (sourceSet in multiplatformExtension.sourceSets) {
//        KotlinSourceSetTree
//        // exclude android source sets, we don't want to mess with their dependencies
//        if (sourceSet.androidSourceSetInfoOrNull != null) continue
//
//        val underlyingSourceSets = multiplatformExtension.findSourceSetsDependingOn(sourceSet)
//        if (underlyingSourceSets.isEmpty()) continue
//
//        val constraintConfiguration = project.configurations.createResolvable(
//            lowerCamelCaseName("kotlin", sourceSet.name, "compileDependenciesConstraints")
//        )
//        constraintConfiguration.description = """
//            This is the internal Kotlin Gradle Plugin configuration! Do not modify or use it!
//            This configuration contains all dependencies that will constraint compile dependencies of '${sourceSet.name}' source set.
//        """.trimIndent()
//        constraintConfiguration.configureMetadataDependenciesAttribute(project)
//        val extenders = underlyingSourceSets.flatMap { it.internal.compileDependenciesConfigurations }
//        constraintConfiguration.extendsFrom(*extenders.toTypedArray())
//
//        sourceSet.internal.resolvableMetadataConfiguration.shouldResolveConsistentlyWith(constraintConfiguration)
//     }
}

private fun Project.configureConsistentDependencyResolution(groupOfSourceSets: Collection<KotlinSourceSet>, configurationName: String) {
    if (groupOfSourceSets.isEmpty()) return
    val configuration = configurations.createResolvable(configurationName)
    configuration.configureMetadataDependenciesAttribute(project)
    val allVisibleSourceSets = groupOfSourceSets + groupOfSourceSets.flatMap { getVisibleSourceSetsFromAssociateCompilations(it) }
    val extenders = allVisibleSourceSets.flatMap { it.internal.compileDependenciesConfigurations }
    configuration.extendsFrom(*extenders.toTypedArray())
    groupOfSourceSets.forEach { it.internal.resolvableMetadataConfiguration.shouldResolveConsistentlyWith(configuration) }
}