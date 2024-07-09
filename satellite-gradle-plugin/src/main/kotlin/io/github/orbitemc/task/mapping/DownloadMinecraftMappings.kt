package io.github.orbitemc.task.mapping

import de.undercouch.gradle.tasks.download.DownloadExtension
import io.github.orbitemc.getVersionMetaFile
import io.github.orbitemc.getVersionProguardMappingFile
import io.github.orbitemc.metatrace.MetaTrace
import io.github.orbitemc.metatrace.meta.MinecraftVersion
import io.github.orbitemc.metatrace.meta.version.VersionData
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class DownloadMinecraftMappings : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        val file = getVersionMetaFile(outputDirectory.get().asFile.toPath(), minecraftVersion.get())
        val version: MinecraftVersion = MetaTrace.readVersionFromFile(file)
            ?: throw GradleException("Was unable to read $file")

        val serverMappings = version.data.downloadEntries[VersionData.SERVER_MAPPINGS]
            ?: throw GradleException("No entry for \"server-mappings\" was found within this version!")
        if (!serverMappings.isValid) throw GradleException("The server mappings are not valid because of mismatched sha values")
        val outputFile = getVersionProguardMappingFile(outputDirectory.get().asFile.toPath(), minecraftVersion.get()).toFile()
        if (outputFile.exists()) return
        project.extensions.getByType(DownloadExtension::class.java).run {
            src(serverMappings.url)
            dest(outputFile)
            overwrite(false)
        }
    }


}
