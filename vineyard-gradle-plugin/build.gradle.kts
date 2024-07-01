plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.jvm)
}

dependencies {
    implementation(project(":vineyard-common"))
}

gradlePlugin {
    val vineyard by plugins.creating {
        id = "io.github.cabernetmc.vineyard"
        implementationClass = "io.github.cabernetmc.VineyardPlugin"
        displayName = "Vineyard"
        description = "Provides an environment for CabernetMC"
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
