rootProject.name = "Vineyard"

gradle.rootProject {
    this.version = "1.0.0-SNAPSHOT"
    this.group = "io.github.cabernetmc"
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
    }
}

rootProject.projectDir.listFiles()!!.filter { it.isDirectory }.forEach { file ->
    if (file.name == "buildSrc") {
        return@forEach
    }

    if (file.listFiles()!!.any { it.name.contains("build.gradle") }) {
        include(":${file.name}")
    }
}
