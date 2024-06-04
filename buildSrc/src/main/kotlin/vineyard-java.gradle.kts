import gradle.kotlin.dsl.accessors._6da0708403d2554780d7bf8b84448399.compileJava
import gradle.kotlin.dsl.accessors._6da0708403d2554780d7bf8b84448399.jar
import gradle.kotlin.dsl.accessors._6da0708403d2554780d7bf8b84448399.publishing
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.publishing

plugins {
    java
    `maven-publish`
}

tasks.compileJava {
    options.encoding = "UTF-8"
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            this.artifact(tasks.jar)
            this.groupId = "com.github.cabernetmc"
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
