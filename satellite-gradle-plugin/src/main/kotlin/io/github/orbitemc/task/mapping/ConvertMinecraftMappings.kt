package io.github.orbitemc.task.mapping

import io.github.orbitemc.SatelliteRemapper
import io.github.orbitemc.asPath
import io.github.orbitemc.getProguardMappings
import io.github.orbitemc.getTinyMappings
import net.fabricmc.mappingio.format.MappingFormat
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.exists

abstract class ConvertMinecraftMappings : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        val output = outputDirectory.asPath()
        val proguardMappings = getProguardMappings(output, minecraftVersion.get())
        val tinyMappings = getTinyMappings(output, minecraftVersion.get())
        if (tinyMappings.exists()) return

        SatelliteRemapper.convertMappings(proguardMappings, MappingFormat.PROGUARD_FILE, tinyMappings)
    }
}
