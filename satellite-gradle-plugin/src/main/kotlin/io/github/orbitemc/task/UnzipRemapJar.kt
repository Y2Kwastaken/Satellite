package io.github.orbitemc.task

import io.github.orbitemc.SatelliteUtils
import io.github.orbitemc.getClassesFolder
import io.github.orbitemc.getRemappedServer
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class UnzipRemapJar : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        val output = outputDirectory.get().asFile.toPath()
        val remapJar = getRemappedServer(output, minecraftVersion.get())
        val unzipOutput = getClassesFolder(output, minecraftVersion.get())
        if (Files.exists(unzipOutput)) return

        SatelliteUtils.unzip(
            remapJar,
            unzipOutput,
            { it.contains("com/mojang") || it.contains("net/minecraft") },
            { logger.lifecycle(it) }
        )
    }

}
