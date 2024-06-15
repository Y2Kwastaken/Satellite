import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.github.goooler.shadow")
    id("com.gradle.plugin-publish")
}

val shade: Configuration by configurations.creating
configurations.implementation {
    extendsFrom(shade)
}

fun ShadowJar.configureStandard() {
    configurations = listOf(shade)

    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*:.*"))
    }

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "OSGI-INF/**",
        "*.profile",
        "module-info.class",
        "ant_tasks/**"
    )

    mergeServiceFiles()
}

tasks.existing(AbstractArchiveTask::class) {
    from(
        zipTree(
            project(":vineyard-core").tasks.named("sourcesJar", AbstractArchiveTask::class).flatMap { it.archiveFile }
        )
    ) {
        exclude("META-INF/**")
    }
}

tasks.existing(ShadowJar::class) {
    archiveClassifier = ""
    configureStandard()
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            shadow.component(this)
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
