/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.gradle.dsl

import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import java.util.*

fun main() {
    generateAbstractKotlinArtifactsExtensionImplementation()
}

private fun generateAbstractKotlinArtifactsExtensionImplementation() {
    val className = typeName("org.jetbrains.kotlin.gradle.targets.native.tasks.artifact.KotlinArtifactsExtensionImpl")

    val imports = """
        import org.gradle.api.Project
        import org.jetbrains.kotlin.gradle.dsl.KotlinArtifact
        import org.jetbrains.kotlin.gradle.dsl.KotlinArtifactConfig
        import org.jetbrains.kotlin.gradle.dsl.KotlinArtifactsExtension
        import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
        import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
        import org.jetbrains.kotlin.konan.target.DeprecatedTargetAPI
        import org.jetbrains.kotlin.konan.target.KonanTarget
        import javax.inject.Inject
    """.trimIndent()

    val generatedCodeWarning = "// DO NOT EDIT MANUALLY! Generated by ${object {}.javaClass.enclosingClass.name}"

    val overrides = listOf(
        "override val artifactConfigs = project.objects.domainObjectSet(KotlinArtifactConfig::class.java)",
        "override val artifacts = project.objects.namedDomainObjectSet(KotlinArtifact::class.java)",
        "override val Native = project.objects.newInstance(KotlinNativeArtifactDSLImpl::class.java, project)"
    ).joinToString("\n").indented(4)

    val buildTypeConstants = NativeBuildType.values().joinToString("\n") {
        "val ${it.name} = NativeBuildType.${it.name}"
    }.indented(4)

    val bitcodeModeConstants = BitcodeEmbeddingMode.values().joinToString(
        separator = "\n",
        prefix = "class BitcodeEmbeddingModeDsl {\n",
        postfix = "\n}"
    ) {
        "val ${it.name} = BitcodeEmbeddingMode.${it.name}".indented(4)
    }.indented(4)

    val bitcodeMode = listOf(
        "@JvmField",
        "val EmbedBitcodeMode = BitcodeEmbeddingModeDsl()"
    ).joinToString("\n").indented(4)

    val konanTargetConstants = KonanTarget.predefinedTargets.values.joinToString("\n") { target ->
        val nameParts = target.name.split("_")
        val name = nameParts.drop(1).joinToString(
            separator = "",
            prefix = nameParts.first(),
            transform = String::capitalizeUS
        )
        val deprecation = "@DeprecatedTargetAPI\n".takeIf { KonanTarget.deprecatedTargets.contains(target) } ?: ""
        "${deprecation}val $name = KonanTarget.${target.name.uppercase(Locale.US)}"
    }.indented(4)

    val code = listOf(
        "package ${className.packageName()}",
        imports,
        generatedCodeWarning,
        "abstract class ${className.shortName()} @Inject constructor(project: Project) : KotlinArtifactsExtension {",
        overrides,
        buildTypeConstants,
        bitcodeModeConstants,
        bitcodeMode,
        konanTargetConstants,
        "}"
    ).joinToString(separator = "\n\n")

    val targetFile = File("$outputSourceRoot/${className.fqName.replace(".", "/")}.kt")
    targetFile.writeText(code)
}