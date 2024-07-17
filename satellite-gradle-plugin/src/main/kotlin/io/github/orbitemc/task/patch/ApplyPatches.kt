package io.github.orbitemc.task.patch

import io.codechicken.diffpatch.cli.PatchOperation
import io.codechicken.diffpatch.util.Input.FolderMultiInput
import io.codechicken.diffpatch.util.LogLevel
import io.codechicken.diffpatch.util.Output.*
import io.github.orbitemc.asPath
import io.github.orbitemc.patcher.SafeFolderMultiOutput
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Patches should not be cached")
abstract class ApplyPatches : DefaultTask() {

    @get:InputDirectory
    abstract val decompileSource: DirectoryProperty

    @get:InputDirectory
    abstract val javaSource: DirectoryProperty

    @get:InputDirectory
    abstract val patchDirectory: DirectoryProperty

    @TaskAction
    fun execute() {
        val patcher = PatchOperation.builder()
            .logTo { logger.lifecycle(it) }
            .level(LogLevel.ALL)
            .baseInput(FolderMultiInput(decompileSource.asPath()))
            .patchesInput(FolderMultiInput(patchDirectory.asPath()))
            .patchedOutput(SafeFolderMultiOutput(javaSource.asPath(), decompileSource.asPath()))
            .summary(true)
            .build()

        val result = patcher.operate()
        logger.lifecycle("Applied ${result.summary?.changedFiles} patches")
    }
}
