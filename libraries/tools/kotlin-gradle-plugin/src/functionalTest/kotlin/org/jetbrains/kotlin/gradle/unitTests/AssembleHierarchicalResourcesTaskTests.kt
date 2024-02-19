/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.unitTests

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnostics
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.AssembleHierarchicalResourcesTask
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.KotlinTargetResourcesPublication
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.registerAssembleHierarchicalResourcesTaskProvider
import org.jetbrains.kotlin.gradle.util.assertContainsDiagnostic
import org.jetbrains.kotlin.gradle.util.buildProjectWithMPP
import org.jetbrains.kotlin.gradle.util.runLifecycleAwareTest
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class AssembleHierarchicalResourcesTaskTests {

    @Test
    fun `test copying order - matches default target hierarchy`() {
        buildProjectWithMPP().runLifecycleAwareTest {
            val kotlin = project.multiplatformExtension
            val compilation = kotlin.linuxArm64().compilations.getByName("main")

            assertEquals(
                listOf(
                    listOf("commonMain"),
                    listOf("nativeMain"),
                    listOf("linuxMain"),
                    listOf("linuxArm64Main"),
                ),
                resourceDirectoriesCopyingOrder(
                    compilation
                )
            )
        }
    }

    @Test
    fun `test copying order - with additional source sets in platform source set`() {
        buildProjectWithMPP().runLifecycleAwareTest {
            val kotlin = project.multiplatformExtension
            val compilation = kotlin.jvm().compilations.getByName("main")

            val a = kotlin.sourceSets.create("a")

            compilation.defaultSourceSet.dependsOn(a)

            assertEquals(
                listOf(
                    listOf("a", "commonMain"),
                    listOf("jvmMain"),
                ),
                resourceDirectoriesCopyingOrder(
                    compilation
                )
            )
        }
    }

    @Test
    fun `test copying order - with source sets more remote than common`() {
        buildProjectWithMPP().runLifecycleAwareTest {
            val kotlin = project.multiplatformExtension
            val compilation = kotlin.jvm().compilations.getByName("main")

            val a = kotlin.sourceSets.create("a")
            val b = kotlin.sourceSets.create("b")
            val c = kotlin.sourceSets.create("c")
            val d = kotlin.sourceSets.create("d")
            val e = kotlin.sourceSets.create("e")

            compilation.defaultSourceSet.dependsOn(a)
            compilation.defaultSourceSet.dependsOn(b)
            a.dependsOn(c)
            b.dependsOn(d)
            d.dependsOn(e)

            assertEquals(
                listOf(
                    listOf("e"),
                    listOf("c", "d"),
                    listOf("a", "b", "commonMain"),
                    listOf("jvmMain"),
                ),
                resourceDirectoriesCopyingOrder(
                    compilation
                )
            )
        }
    }

    @Test
    fun `test registering multiple resources assembling tasks - results in a diagnostic`() {
        buildProjectWithMPP().runLifecycleAwareTest {
            val kotlin = project.multiplatformExtension
            val compilation = kotlin.jvm().compilations.getByName("main")

            registerFakeResourcesTask(compilation)
            registerFakeResourcesTask(compilation)

            assertContainsDiagnostic(KotlinToolingDiagnostics.ResourcePublishedMoreThanOncePerTarget)
        }
    }

    private suspend fun Project.resourceDirectoriesCopyingOrder(
        compilation: KotlinCompilation<*>,
    ): List<List<String>> {
        return registerFakeResourcesTask(compilation).get().resourceDirectoriesByLevel.get().map { resourcesLevel ->
            resourcesLevel.map { resource ->
                resource.absolutePath.get().name
            }
        }
    }

    private suspend fun Project.registerFakeResourcesTask(compilation: KotlinCompilation<*>): TaskProvider<AssembleHierarchicalResourcesTask> {
        return compilation.registerAssembleHierarchicalResourcesTaskProvider(
            "test",
            resources = KotlinTargetResourcesPublication.TargetResources(
                { ss ->
                    KotlinTargetResourcesPublication.ResourceRoot(
                        provider { File(ss.name) },
                        emptyList(),
                        emptyList(),
                    )
                },
                provider { File("stub") }
            )
        )
    }

}