/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.resources

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import java.io.File

@InternalKotlinGradlePluginApi
interface KotlinTargetResourcesPublication {

    data class TargetResources(
        val resourcePathForSourceSet: (KotlinSourceSet) -> (ResourceRoot),
        val relativeResourcePlacement: Provider<File>,
    )

    data class ResourceRoot(
        val absolutePath: Provider<File>,
        val includes: List<String>,
        val excludes: List<String>,
    )

    companion object {
        const val EXTENSION_NAME = "multiplatformResourcesPublication"
    }

}