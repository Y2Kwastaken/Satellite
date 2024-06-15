package io.github.cabernetmc

import io.github.cabernetmc.extension.VineyardSettings
import io.github.cabernetmc.task.MinecraftMetaTask
import io.github.cabernetmc.task.VineyardDownloadTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class VineyardInternalsPlugin : Plugin<Project> {


    override fun apply(project: Project) {
        val vineyardSettings = VineyardSettings(project)
        project.extensions.add(VINEYARD_EXTENSION, vineyardSettings)

        project.tasks.register(VINEYARD_SETUP_MINECRAFT_META, MinecraftMetaTask::class.java) {
            group = VINEYARD_SETUP_GROUP
            this.settings.set(vineyardSettings)
        }

        project.tasks.register(VINEYARD_SETUP_DOWNLOAD, VineyardDownloadTask::class.java) {
            group = VINEYARD_SETUP_GROUP
            val metaTask = project.tasks.getByName(VINEYARD_SETUP_MINECRAFT_META) as MinecraftMetaTask
            dependsOn(metaTask)
            this.settings.set(vineyardSettings)
            this.metadata.set(metaTask.metadata.get())
        }
    }
}
