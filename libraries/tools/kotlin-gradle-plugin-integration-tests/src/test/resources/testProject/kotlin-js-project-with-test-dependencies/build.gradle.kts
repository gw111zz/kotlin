plugins {
    kotlin("multiplatform")
}

group = "com.example"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        nodejs {
        }
    }
}

dependencies {
    "jsTestRuntimeOnly"(npm("xmlhttprequest", "1.8.0"))
}