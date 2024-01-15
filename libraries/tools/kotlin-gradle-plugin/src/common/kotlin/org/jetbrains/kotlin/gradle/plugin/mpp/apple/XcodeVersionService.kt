/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.apple

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinProjectSetupAction
import org.jetbrains.kotlin.gradle.plugin.PropertiesProvider.Companion.kotlinPropertiesProvider
import org.jetbrains.kotlin.gradle.plugin.diagnostics.BuildServiceUsingKotlinToolingDiagnostics
import org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnostics.XcodeVersionTooHighWarning
import org.jetbrains.kotlin.gradle.plugin.diagnostics.setupKotlinToolingDiagnosticsParameters
import org.jetbrains.kotlin.gradle.plugin.launch
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.withType
import org.jetbrains.kotlin.gradle.utils.registerClassLoaderScopedBuildService
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.Xcode
import org.jetbrains.kotlin.konan.target.XcodeVersion

internal val XcodeServiceSetupAction = KotlinProjectSetupAction {
    launch {
        val hasAppleTargets = multiplatformExtension.awaitTargets().any { it is KotlinNativeTarget && it.konanTarget.family.isAppleFamily }
        if (hasAppleTargets) {
            val serviceProvider = XcodeVersionService.registerIfAbsent(this@KotlinProjectSetupAction)

            tasks.withType<UsesXcodeVersionService>().configureEach {
                it.usesService(serviceProvider)
                it.xcodeVersionService.set(serviceProvider)
            }
        }

    }
}

internal interface UsesXcodeVersionService : Task {
    @get:Internal
    val xcodeVersionService: Property<XcodeVersionService>
}

internal abstract class XcodeVersionService : BuildServiceUsingKotlinToolingDiagnostics<XcodeVersionService.Parameters> {

    interface Parameters : BuildServiceUsingKotlinToolingDiagnostics.Parameters {
        val ignoreVersionCompatibilityCheck: Property<Boolean>
    }

    companion object {
        fun registerIfAbsent(project: Project): Provider<XcodeVersionService> {
            return project.gradle.registerClassLoaderScopedBuildService(XcodeVersionService::class) { spec ->
                spec.parameters.setupKotlinToolingDiagnosticsParameters(project)
                spec.parameters.ignoreVersionCompatibilityCheck.convention(project.kotlinPropertiesProvider.appleIgnoreXcodeVersionCompatibility)
            }
        }
    }

    private val logger = Logging.getLogger(this::class.java)

    /**
     * Non-null on macOS, null otherwise
     */
    val version: XcodeVersion? = if (HostManager.hostIsMac) {
        Xcode.findCurrent().version.also(::checkVersionCompatibility)
    } else {
        null
    }

    private fun checkVersionCompatibility(xcodeVersion: XcodeVersion) = with(parameters) {
        if (!ignoreVersionCompatibilityCheck.get() && xcodeVersion > XcodeVersion.maxTested) {
            toolingDiagnosticsCollector.get().report(
                this, logger,
                XcodeVersionTooHighWarning(
                    xcodeVersionString = xcodeVersion.toString(),
                    maxTested = XcodeVersion.maxTested.toString(),
                )
            )
        }
    }
}
