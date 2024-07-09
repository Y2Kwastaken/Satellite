package io.github.orbitemc.task.server

import io.github.orbitemc.SatelliteRemapper
import io.github.orbitemc.getVersionServerObfuscatedFile
import io.github.orbitemc.getVersionServerRemapFile
import io.github.orbitemc.getVersionTinyMappingFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class RemapMinecraftServerJar : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        val output = outputDirectory.get().asFile.toPath()
        val obfuscatedJar = getVersionServerObfuscatedFile(output, minecraftVersion.get())
        val remapJar = getVersionServerRemapFile(output, minecraftVersion.get())
        if (Files.exists(remapJar)) return
        val tinyMappings = getVersionTinyMappingFile(output, minecraftVersion.get())

        SatelliteRemapper.remapJarFile(
            obfuscatedJar, remapJar, tinyMappings, false
        )
    }

}
