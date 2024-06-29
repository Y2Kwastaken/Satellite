import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    idea
    id("org.gradle.kotlin.kotlin-dsl")
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}
