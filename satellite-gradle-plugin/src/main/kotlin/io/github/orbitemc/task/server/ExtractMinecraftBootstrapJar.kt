package io.github.orbitemc.task

import io.github.orbitemc.BOOTSTRAP_JAR_META_LOC
import io.github.orbitemc.LIBRARIES_LIST_META_LOC
import io.github.orbitemc.SATELLITE
import io.github.orbitemc.SatelliteUtils
import io.github.orbitemc.getVersionBootstrapFile
import io.github.orbitemc.getVersionLibrariesFile
import io.github.orbitemc.getVersionServerFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ExtractMinecraftBootstrapJar : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    init {
        outputDirectory.convention { project.file(SATELLITE) }
    }

    @TaskAction
    fun execute() {
        val output = outputDirectory.get().asFile.toPath()
        val bootstrap = getVersionBootstrapFile(output, minecraftVersion.get())
        SatelliteUtils.jarExtract(
            bootstrap,
            getVersionServerFile(output, minecraftVersion.get()),
            BOOTSTRAP_JAR_META_LOC.format(minecraftVersion.get(), minecraftVersion.get())
        )
        SatelliteUtils.jarExtract(
            bootstrap, getVersionLibrariesFile(output, minecraftVersion.get()), LIBRARIES_LIST_META_LOC
        )
    }

}
