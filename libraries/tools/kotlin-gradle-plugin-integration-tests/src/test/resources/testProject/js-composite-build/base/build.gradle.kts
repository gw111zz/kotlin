group = "com.example"

plugins {
    kotlin("multiplatform") version "<pluginMarkerVersion>"
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        nodejs()
        browser()

        this@kotlin.sourceSets {
            val jsMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                    implementation(npm("decamelize", "1.1.1"))
                }
            }
        }
    }
}

tasks.named("jsBrowserTest") {
    enabled = false
}

rootProject.tasks
    .withType(org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask::class.java)
    .named("kotlinNpmInstall")
    .configure {
        args.addAll(
            listOf(
                "--network-concurrency",
                "1",
                "--mutex",
                "network"
            )
        )
    }