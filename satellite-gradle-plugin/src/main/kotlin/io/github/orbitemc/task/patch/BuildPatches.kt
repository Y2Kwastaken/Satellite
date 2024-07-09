package io.github.orbitemc.task.patch

import io.codechicken.diffpatch.cli.DiffOperation
import io.codechicken.diffpatch.util.Input
import io.codechicken.diffpatch.util.Input.FolderMultiInput
import io.codechicken.diffpatch.util.LogLevel
import io.codechicken.diffpatch.util.Output
import io.codechicken.diffpatch.util.Output.FolderMultiOutput
import io.github.orbitemc.asPath
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Patches should not be cached")
abstract class BuildPatches : DefaultTask() {

    @get:InputDirectory
    abstract val decompileSource: DirectoryProperty

    @get:InputDirectory
    abstract val javaSource: DirectoryProperty

    @get:OutputDirectory
    abstract val patchDirectory: DirectoryProperty

    @TaskAction
    fun execute() {
        val differ = DiffOperation.builder()
            .logTo { logger.lifecycle(it) }
            .level(LogLevel.ALL)
            .baseInput(FolderMultiInput(decompileSource.asPath()))
            .changedInput(FolderMultiInput(javaSource.asPath()))
            .patchesOutput(FolderMultiOutput(patchDirectory.asPath()))
            .context(3)
            .summary(true)
            .autoHeader(true)
            .build()

        val result = differ.operate()
        logger.lifecycle("Built ${result.summary?.changedFiles} patches")
    }

}
