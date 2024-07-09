package io.github.orbitemc.task

import io.github.orbitemc.SATELLITE
import io.github.orbitemc.SatelliteUtils
import io.github.orbitemc.getVersionDecompileClassDir
import io.github.orbitemc.getVersionServerRemapFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class UnzipRemapJarTak : DefaultTask() {

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
        val remapJar = getVersionServerRemapFile(output, minecraftVersion.get())
        val unzipOutput = getVersionDecompileClassDir(output, minecraftVersion.get())

        SatelliteUtils.unzip(
            remapJar,
            unzipOutput,
            {}
        )
    }

}
