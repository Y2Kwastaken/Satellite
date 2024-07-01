package io.github.cabernetmc

import io.github.cabernetmc.extension.VineyardSettings
import org.gradle.api.Plugin
import org.gradle.api.Project

class VineyardPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val vineyardSettings = VineyardSettings(project)
        project.extensions.add(VINEYARD_EXTENSION, vineyardSettings)
    }
}
