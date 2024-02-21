package org.jetbrains.kotlin.gradle.mpp.resources

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.incremental.testingUtils.assertEqualDirectoriesIgnoringDotFiles
import org.junit.jupiter.api.DisplayName
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.name
import kotlin.io.path.writeText

@MppGradlePluginTests
@AndroidTestVersions(minVersion = TestVersions.AGP.AGP_73)
@DisplayName("Test multiplatform resources publication")
class MultiplatformResourcesPublicationIT : KGPBaseTest() {

    @DisplayName("Multiplatform resources publication for Android target with release build type")
    @GradleAndroidTest
    fun testAndroidReleaseResourcesPublication(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        project(
            "multiplatformResources/publication",
            gradleVersion,
            buildJdk = providedJdk.location,
        ) {
            buildWithAGPVersion(
                ":publishAndroidReleasePublicationToMavenRepository",
                androidVersion,
                defaultBuildOptions,
            )
            val publishedAarPath = "build/repo/test/publication-android/1.0/publication-android-1.0.aar"
            val classesInAar = projectPath.resolve("classesInAar")
            val classesJar = "classes.jar"
            unzip(
                projectPath.resolve(publishedAarPath),
                classesInAar,
                filesStartingWith = classesJar
            )
            compareEmbeddedResources(
                inputZip = classesInAar.resolve(classesJar),
                reference = reference("androidMain")
            )

            val assetsInAar = projectPath.resolve("assetsInAar")
            unzip(
                projectPath.resolve(publishedAarPath),
                assetsInAar,
                filesStartingWith = "assets"
            )
            assertEqualDirectoriesIgnoringDotFiles(
                assetsInAar.toFile(),
                reference("androidFonts").toFile(),
                forgiveOtherExtraFiles = false,
            )
        }
    }

    @DisplayName("Multiplatform resources publication for jvm target")
    @GradleAndroidTest
    fun testJvmResourcesPublication(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        testEmbeddedResources(
            gradleVersion,
            androidVersion,
            providedJdk,
            publicationTask = ":publishJvmPublicationToMavenRepository",
            publishedArchive = "build/repo/test/publication-jvm/1.0/publication-jvm-1.0.jar",
            referenceName = "jvm",
        )
    }

    @DisplayName("Multiplatform resources publication for Native target")
    @GradleAndroidTest
    fun testNativeTargetResourcesPublication(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        testEmbeddedResources(
            gradleVersion,
            androidVersion,
            providedJdk,
            publicationTask = ":publishLinuxX64PublicationToMavenRepository",
            publishedArchive = "build/repo/test/publication-linuxx64/1.0/publication-linuxx64-1.0-kotlin_resources.kotlin_resources.zip",
            referenceName = "linuxX64",
        )
    }

    @DisplayName("Multiplatform resources publication for wasm js target")
    @GradleAndroidTest
    fun testWasmJsTargetResourcesPublication(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        testEmbeddedResources(
            gradleVersion,
            androidVersion,
            providedJdk,
            publicationTask = ":publishWasmJsPublicationToMavenRepository",
            publishedArchive = "build/repo/test/publication-wasm-js/1.0/publication-wasm-js-1.0-kotlin_resources.kotlin_resources.zip",
            referenceName = "wasmJs",
        )
    }

    @DisplayName("Multiplatform resources publication for wasm wasi target")
    @GradleAndroidTest
    fun testWasmWasiTargetResourcesPublication(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        testEmbeddedResources(
            gradleVersion,
            androidVersion,
            providedJdk,
            publicationTask = ":publishWasmWasiPublicationToMavenRepository",
            publishedArchive = "build/repo/test/publication-wasm-wasi/1.0/publication-wasm-wasi-1.0-kotlin_resources.kotlin_resources.zip",
            referenceName = "wasmWasi",
        )
    }

    @DisplayName("Multiplatform resources publication when a previously non-existent source set with resource is added")
    @GradleAndroidTest
    fun testNativeTargetResourcesPublicationWithNewSourceSet(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
    ) {
        project(
            "multiplatformResources/publication",
            gradleVersion,
            buildJdk = providedJdk.location,
        ) {
            val publishedArchive = projectPath.resolve(
                "build/repo/test/publication-linuxx64/1.0/publication-linuxx64-1.0-kotlin_resources.kotlin_resources.zip"
            )

            buildWithAGPVersion(
                ":publishLinuxX64PublicationToMavenRepository",
                androidVersion = androidVersion,
                defaultBuildOptions = defaultBuildOptions,
            )
            compareEmbeddedResources(
                publishedArchive,
                reference("linuxX64")
            )

            // Add a file to a source set that didn't exist previously
            val linuxMainSourceSet = projectPath.resolve("src/linuxMain")
            assertDirectoryDoesNotExist(linuxMainSourceSet)
            val newResource = linuxMainSourceSet.resolve("multiplatformResources/newSourceSetResource")
            assert(newResource.parent.toFile().mkdirs())
            newResource.writeText(newResource.name)

            buildWithAGPVersion(
                ":publishLinuxX64PublicationToMavenRepository",
                androidVersion = androidVersion,
                defaultBuildOptions = defaultBuildOptions,
            )
            compareEmbeddedResources(
                publishedArchive,
                reference("linuxX64WithNewSourceSet")
            )
        }
    }

    private fun testEmbeddedResources(
        gradleVersion: GradleVersion,
        androidVersion: String,
        providedJdk: JdkVersions.ProvidedJdk,
        publicationTask: String,
        publishedArchive: String,
        referenceName: String,
    ) {
        project(
            "multiplatformResources/publication",
            gradleVersion,
            buildJdk = providedJdk.location,
        ) {
            buildWithAGPVersion(
                publicationTask,
                androidVersion,
                defaultBuildOptions,
            )
            compareEmbeddedResources(
                projectPath.resolve(publishedArchive),
                reference(referenceName)
            )
        }
    }

    private fun TestProject.reference(
        named: String
    ): Path = projectPath.resolve("reference/$named")

    private fun TestProject.compareEmbeddedResources(
        inputZip: Path,
        reference: Path,
    ) {
        val publishedResources = projectPath.resolve("published/${reference.name}")
        unzip(
            inputZip = inputZip,
            outputDir = publishedResources,
            filesStartingWith = "embed",
        )
        assertDirectoryExists(publishedResources)
        assertDirectoryExists(reference)
        assertEqualDirectoriesIgnoringDotFiles(
            publishedResources.toFile(),
            reference.toFile(),
            forgiveOtherExtraFiles = false,
        )
    }

}