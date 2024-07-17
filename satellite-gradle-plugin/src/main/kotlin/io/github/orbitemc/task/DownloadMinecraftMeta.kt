package io.github.orbitemc.task

import io.github.orbitemc.asPath
import io.github.orbitemc.getMetaFile
import io.github.orbitemc.metatrace.MetaTrace
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class DownloadMinecraftMeta : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        logger.lifecycle("Obtaining version data for version ${minecraftVersion.get()}")
        val version = MetaTrace.getVersion(minecraftVersion.get())
        val outputFile = getMetaFile(outputDirectory.asPath(), minecraftVersion.get())
        MetaTrace.saveVersionToFile(version, outputFile)
    }

}
