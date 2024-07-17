plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.jvm)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":satellite-common"))
    implementation(libs.gradle.download)
    implementation(libs.gradle.checksum)
    implementation(libs.metatrace)
}

gradlePlugin {
    val vineyard by plugins.creating {
        id = "io.github.orbitemc.satellite"
        implementationClass = "io.github.orbitemc.SatellitePlugin"
        displayName = "Satellite"
        description = "Provides an environment for OrbitMC"
    }
}

publishing {
    repositories {
        maven("https://maven.miles.sh/snapshots") {
            credentials {
                this.username = System.getenv("CABERNETMC_REPOSILITE_USERNAME")
                this.password = System.getenv("CABERNETMC_REPOSILITE_PASSWORD")
            }
        }
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}
