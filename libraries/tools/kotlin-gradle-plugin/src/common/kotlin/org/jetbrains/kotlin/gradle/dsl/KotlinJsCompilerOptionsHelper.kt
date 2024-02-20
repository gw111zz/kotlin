// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
// To regenerate run 'generateGradleOptions' task
@file:Suppress("RemoveRedundantQualifierName", "Deprecation", "Deprecation_Error", "DuplicatedCode")

package org.jetbrains.kotlin.gradle.dsl

internal object KotlinJsCompilerOptionsHelper {

    internal fun fillCompilerArguments(
        from: org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions,
        args: org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments,
    ) {
        org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptionsHelper.fillCompilerArguments(from, args)
        args.friendModulesDisabled = from.friendModulesDisabled.get()
        args.main = from.main.get().mode
        args.metaInfo = from.metaInfo.get()
        args.moduleKind = from.moduleKind.get().kind
        args.moduleName = from.moduleName.orNull
        args.noStdlib = from.noStdlib.get()
        args.sourceMap = from.sourceMap.get()
        args.sourceMapEmbedSources = from.sourceMapEmbedSources.orNull?.mode
        args.sourceMapNamesPolicy = from.sourceMapNamesPolicy.orNull?.policy
        args.sourceMapPrefix = from.sourceMapPrefix.orNull
        args.target = from.target.get()
        args.typedArrays = from.typedArrays.get()
        args.useEsClasses = from.useEsClasses.get()
    }

    internal fun syncOptionsAsConvention(
        from: org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions,
        into: org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions,
    ) {
        org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptionsHelper.syncOptionsAsConvention(from, into)
        into.friendModulesDisabled.convention(from.friendModulesDisabled)
        into.main.convention(from.main)
        into.metaInfo.convention(from.metaInfo)
        into.moduleKind.convention(from.moduleKind)
        into.moduleName.convention(from.moduleName)
        into.noStdlib.convention(from.noStdlib)
        into.sourceMap.convention(from.sourceMap)
        into.sourceMapEmbedSources.convention(from.sourceMapEmbedSources)
        into.sourceMapNamesPolicy.convention(from.sourceMapNamesPolicy)
        into.sourceMapPrefix.convention(from.sourceMapPrefix)
        into.target.convention(from.target)
        into.typedArrays.convention(from.typedArrays)
        into.useEsClasses.convention(from.useEsClasses)
    }
}
