plugins {
    java
    `maven-publish`
    kotlin("jvm")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "21"
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            this.artifact(tasks.jar)
            this.groupId = "io.github.cabernetmc"
            this.version = rootProject.version.toString()
        }
    }

    repositories {
        maven("https://maven.miles.sh/snapshots") {
            credentials {
                this.username = System.getenv("CABERNETMC_REPOSILITE_USERNAME")
                this.password = System.getenv("CABERNETMC_REPOSILITE_PASSWORD")
            }
        }
    }
}
