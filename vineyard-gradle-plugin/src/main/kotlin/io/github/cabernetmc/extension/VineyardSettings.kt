package io.github.cabernetmc.extension

import io.github.cabernetmc.execution.VineyardExecutionSettings
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import java.io.File

class VineyardSettings(val project: Project) {
    @Input
    var version: String? = null

    @Input
    var debug: Boolean = false

    @Input
    var ignoreCaches: Boolean = false

    @InputDirectory
    var workingDirectory: File? = null

    fun asExecutionSettings(): VineyardExecutionSettings {
        val builder = VineyardExecutionSettings.Builder()
        if (this.debug) builder.debug { s -> project.logger.lifecycle(s) }
        if (this.ignoreCaches) builder.ignoreCaches()
        builder.workingDirectory(this.workingDirectory!!.toPath())
        return builder.build(this.version!!)
    }
}
