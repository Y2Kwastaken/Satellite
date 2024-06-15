import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    idea
    id("org.gradle.kotlin.kotlin-dsl")
}

java {
    withSourcesJar()
}

tasks.withType(JavaCompile::class.java) {
    options.release = 21
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

configurations.all {
    if (name == "compileOnly") {
        return@all
    }
    dependencies.remove(project.dependencies.gradleApi())
    dependencies.removeIf { it.group == "org.jetbrains.kotlin" }
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Version" to project.version
        )
    }
}
