// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
package org.jetbrains.kotlin.gradle.dsl

@Suppress("DEPRECATION")
interface KotlinCommonOptions  : org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions {

    /**
     * Allow using declarations only from the specified version of bundled libraries
     * Possible values: "1.4 (DEPRECATED)", "1.5", "1.6", "1.7 (EXPERIMENTAL)"
     * Default value: null
     */
     var apiVersion: kotlin.String?

    /**
     * Provide source compatibility with the specified version of Kotlin
     * Possible values: "1.4 (DEPRECATED)", "1.5", "1.6", "1.7 (EXPERIMENTAL)"
     * Default value: null
     */
     var languageVersion: kotlin.String?

    /**
     * Compile using Front-end IR. Warning: this feature is far from being production-ready
     * Default value: false
     */
     var useFir: kotlin.Boolean
}
