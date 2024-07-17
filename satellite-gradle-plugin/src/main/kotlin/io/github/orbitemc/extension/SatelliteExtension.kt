package io.github.orbitemc.extension

import io.github.orbitemc.SATELLITE
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

abstract class SatelliteExtension(project: Project, factory: ObjectFactory) {
    val minecraftVersion = factory.property(String::class.java)
    val decompilerDownloadLink = factory.property(String::class.java)
    val decompilerArguments = factory.listProperty(String::class.java)
    val workingDirectory = factory.fileProperty()

    @Deprecated(message = "buildDirectory will eventually be brought in line with workingDirectory")
    val buildDirectory = factory.fileProperty()

    init {
        decompilerDownloadLink.convention("https://s01.oss.sonatype.org/content/repositories/snapshots/org/vineflower/vineflower/1.11.0-SNAPSHOT/vineflower-1.11.0-20240522.034251-27.jar")
        decompilerArguments.convention(listOf("--explicit-generics=1"))
        workingDirectory.convention(project.layout.buildDirectory.file(SATELLITE))
        buildDirectory.convention(project.layout.buildDirectory.file(SATELLITE))
    }
}
