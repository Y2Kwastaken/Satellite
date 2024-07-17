package io.github.orbitemc.task.server

import io.github.orbitemc.BOOTSTRAP_JAR_META_LOC
import io.github.orbitemc.LIBRARIES_LIST_META_LOC
import io.github.orbitemc.SatelliteUtils
import io.github.orbitemc.getBootstrapJar
import io.github.orbitemc.getLibrariesList
import io.github.orbitemc.getObfuscatedServer
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class ExtractMinecraftBootstrapJar : DefaultTask() {

    @get:Input
    abstract val minecraftVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: RegularFileProperty

    @TaskAction
    fun execute() {
        val output = outputDirectory.get().asFile.toPath()
        val bootstrap = getBootstrapJar(output, minecraftVersion.get())
        val obfuscatedJarFile = getObfuscatedServer(output, minecraftVersion.get())
        if (Files.notExists(obfuscatedJarFile)) {
            SatelliteUtils.jarExtract(
                bootstrap,
                obfuscatedJarFile,
                BOOTSTRAP_JAR_META_LOC.format(minecraftVersion.get(), minecraftVersion.get())
            )
        }
        val librariesList = getLibrariesList(output, minecraftVersion.get())
        if (Files.notExists(librariesList)) {
            SatelliteUtils.jarExtract(
                bootstrap, librariesList, LIBRARIES_LIST_META_LOC
            )
        }
    }

}
