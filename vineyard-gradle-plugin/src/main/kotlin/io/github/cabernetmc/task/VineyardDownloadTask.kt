package io.github.cabernetmc.task

import io.github.cabernetmc.execution.impl.VineyardDownloadExecution
import io.github.cabernetmc.extension.VineyardSettings
import io.github.cabernetmc.meta.MinecraftVersion
import io.github.cabernetmc.util.Result.Failure
import io.github.cabernetmc.util.Result.Success
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class VineyardDownloadTask : DefaultTask() {

    @get:Internal
    val settings: Property<VineyardSettings> = project.objects.property(VineyardSettings::class.java)

    @get:Internal
    val metadata: Property<MinecraftVersion> = project.objects.property(MinecraftVersion::class.java)

    @TaskAction
    fun execute() {
        val settings = this.settings.get().asExecutionSettings()

        when(val result = VineyardDownloadExecution(settings, metadata.get()).run()) {
            is Success<*, *> -> return
            is Failure<*, IllegalStateException> -> throw GradleException(result.exception.message!!)
        }
    }

}
