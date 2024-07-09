package io.github.kryptidemc

import io.github.kryptidemc.extension.VineyardExtension
import io.github.kryptidemc.task.DownloadMinecraftMeta
import org.gradle.api.Plugin
import org.gradle.api.Project

class KryptidePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("java")

        val vineyard = project.extensions.create(VINEYARD, VineyardExtension::class.java)

        project.tasks.register(DOWNLOAD_MINECRAFT_META_TASK, DownloadMinecraftMeta::class.java) {
            minecraftVersion.set(vineyard.minecraftVersion)
            outputDirectory.set(project.file(io.github.kryptidemc.VINEYARD))
        }
    }
}
