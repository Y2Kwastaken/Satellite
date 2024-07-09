plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.jvm)
}

dependencies {
    implementation(project(":specter-common"))
    implementation(libs.gradle.download)
    implementation(libs.metatrace)
}

gradlePlugin {
    val vineyard by plugins.creating {
        id = "io.github.kryptidemc.specter"
        implementationClass = "io.github.kryptidemc.SpecterPlugin"
        displayName = "Specter"
        description = "Provides an environment for KryptideMC"
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
