package io.github.kryptidemc.task

import io.github.kryptidemc.VINEYARD
import io.github.cabernetmc.cuvee.Cuvee
import io.github.kryptidemc.getVersionMetaCache
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class DownloadMinecraftMeta : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    init {
        outputDirectory.convention { File(VINEYARD) }
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("Obtaining version data for version ${minecraftVersion.get()}")
        val version = Cuvee.getVersion(minecraftVersion.get())
        val outputFile = getVersionMetaCache(outputDirectory.get().asFile.toPath(), minecraftVersion.get())
        logger.lifecycle("Saving ${minecraftVersion.get()} to $outputFile")
        Cuvee.saveVersionToFile(version, outputFile)
    }

}
