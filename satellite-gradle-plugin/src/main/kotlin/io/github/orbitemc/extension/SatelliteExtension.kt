package io.github.orbitemc.extension

import io.github.orbitemc.SATELLITE
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

abstract class SatelliteExtension(project: Project, factory: ObjectFactory) {
    val minecraftVersion = factory.property(String::class.java)
    val vineflowerVersion = factory.property(String::class.java)
    val decompilerArguments = factory.listProperty(String::class.java)
    val workingDirectory = factory.fileProperty()

    init {
        vineflowerVersion.convention("1.10.1")
        workingDirectory.convention { project.file(SATELLITE) }
        decompilerArguments.convention(
            listOf(
                "-udv=1",
                "-ump=0",
                "-asc=1",
                "-rbr=0"
            )
        )
    }
}
