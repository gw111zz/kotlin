/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.unitTests

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnostics
import org.jetbrains.kotlin.gradle.plugin.diagnostics.ToolingDiagnosticFactory
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.KotlinTargetResourcesPublication
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.resourcesPublicationExtension
import org.jetbrains.kotlin.gradle.util.assertContainsDiagnostic
import org.jetbrains.kotlin.gradle.util.assertNoDiagnostics
import org.jetbrains.kotlin.gradle.util.buildProjectWithMPP
import org.jetbrains.kotlin.gradle.util.kotlin
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class KotlinTargetResourcesPublicationImplTests {

    @Test
    fun `test publication callback - after resources publication`() {
        val project = buildProjectWithMPP {
            kotlin {
                jvm()
            }
        }
        val target = project.multiplatformExtension.jvm()

        testCallbacksAfterApiCall(
            callback = { back ->
                project.multiplatformExtension.resourcesPublicationExtension?.subscribeOnPublishResources(target) {
                    back(Unit)
                }
            },
            apiCall = { project.publishFakeResources(target) }
        )
    }

    @Test
    fun `test assets callback - after resources publication`() {
        val project = mppProjectWithAndroidTarget()
        val target = project.multiplatformExtension.androidTarget()

        testCallbacksAfterApiCall(
            callback = { back ->
                project.multiplatformExtension.resourcesPublicationExtension?.subscribeOnAndroidPublishAssets(target) {
                    back(Unit)
                }
            },
            apiCall = { project.publishFakeAssets(target) }
        )
    }

    @Test
    fun `test publication callback - before and after resources publication`() {
        val project = buildProjectWithMPP {
            kotlin {
                jvm()
            }
        }
        val target = project.multiplatformExtension.jvm()

        testCallbacksBeforeAndAfterApiCall(
            callback = { back ->
                project.multiplatformExtension.resourcesPublicationExtension?.subscribeOnPublishResources(target) {
                    back(Unit)
                }
            },
            apiCall = { project.publishFakeResources(target) }
        )
    }

    @Test
    fun `test assets callback - before and after resources publication`() {
        val project = mppProjectWithAndroidTarget()
        val target = project.multiplatformExtension.androidTarget()

        testCallbacksBeforeAndAfterApiCall(
            callback = { back ->
                project.multiplatformExtension.resourcesPublicationExtension?.subscribeOnAndroidPublishAssets(target) {
                    back(Unit)
                }
            },
            apiCall = { project.publishFakeAssets(target) }
        )
    }

    @Test
    fun `test publication - reports a diagnostic when publishing multiple times per target`() {
        val project = buildProjectWithMPP {
            kotlin {
                jvm()
            }
        }
        val target = project.multiplatformExtension.jvm()

        project.testMultipleApiCallsEmitDiagnostic(
            apiCall = { project.publishFakeResources(target) },
            diagnostic = KotlinToolingDiagnostics.ResourcePublishedMoreThanOncePerTarget,
        )
    }

    @Test
    fun `test assets - reports a diagnostic when publishing multiple times per target`() {
        val project = mppProjectWithAndroidTarget()
        val target = project.multiplatformExtension.androidTarget()

        project.testMultipleApiCallsEmitDiagnostic(
            apiCall = { project.publishFakeAssets(target) },
            diagnostic = KotlinToolingDiagnostics.AssetsPublishedMoreThanOncePerTarget,
        )
    }

    private fun testCallbacksAfterApiCall(
        callback: ((Unit) -> Unit) -> Unit,
        apiCall: (Unit) -> Unit,
    ) {
        var callbacks = 0

        apiCall.invoke(Unit)
        callback {
            callbacks += 1
        }
        assertEquals(callbacks, 1)

        callback {
            callbacks += 1
        }
        assertEquals(callbacks, 2)
    }

    private fun testCallbacksBeforeAndAfterApiCall(
        callback: ((Unit) -> Unit) -> Unit,
        apiCall: (Unit) -> Unit,
    ) {
        var callbacks = 0

        callback {
            callbacks += 1
        }
        assertEquals(callbacks, 0)

        apiCall.invoke(Unit)
        assertEquals(callbacks, 1)

        callback {
            callbacks += 1
        }
        assertEquals(callbacks, 2)
    }

    private fun ProjectInternal.testMultipleApiCallsEmitDiagnostic(
        apiCall: (Unit) -> Unit,
        diagnostic: ToolingDiagnosticFactory,
    ) {
        assertNoDiagnostics(diagnostic)
        apiCall(Unit)
        assertNoDiagnostics(diagnostic)
        apiCall(Unit)
        assertContainsDiagnostic(diagnostic)
    }

    private fun mppProjectWithAndroidTarget(): ProjectInternal {
        val project = buildProjectWithMPP {
            plugins.apply("com.android.library")
            kotlin {
                androidTarget()
            }
        }
        (project.extensions.getByName("android") as LibraryExtension).compileSdk = 30
        return project
    }

    private fun Project.publishFakeResources(target: KotlinTarget) {
        project.multiplatformExtension.resourcesPublicationExtension?.publishResourcesAsKotlinComponent(
            target,
            resourcePathForSourceSet = {
                KotlinTargetResourcesPublication.ResourceRoot(
                    project.provider { File(it.name) },
                    emptyList(),
                    emptyList(),
                )
            },
            relativeResourcePlacement = project.provider { File("test") },
        )
    }

    private fun Project.publishFakeAssets(target: KotlinAndroidTarget) {
        project.multiplatformExtension.resourcesPublicationExtension?.publishInAndroidAssets(
            target,
            resourcePathForSourceSet = {
                KotlinTargetResourcesPublication.ResourceRoot(
                    project.provider { File(it.name) },
                    emptyList(),
                    emptyList(),
                )
            },
            relativeResourcePlacement = project.provider { File("test") },
        )
    }

}