package io.github.orbitemc.task.patch

import io.codechicken.diffpatch.cli.PatchOperation
import io.codechicken.diffpatch.util.Input.FolderMultiInput
import io.codechicken.diffpatch.util.LogLevel
import io.codechicken.diffpatch.util.Output.FolderMultiOutput
import io.codechicken.diffpatch.util.PatchMode
import io.github.orbitemc.asPath
import io.github.orbitemc.patcher.SafeFolderMultiOutput
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "Patches should not be cached")
abstract class ApplyPatchesFuzzy : DefaultTask() {

    @get:InputDirectory
    abstract val decompileSource: DirectoryProperty

    @get:InputDirectory
    abstract val javaSource: DirectoryProperty

    @get:InputDirectory
    abstract val patchDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val failedOutput: DirectoryProperty

    @get:Input
    abstract val minFuzz: Property<Float>

    @TaskAction
    fun execute() {
        val patcher = PatchOperation.builder()
            .logTo{ logger.lifecycle(it) }
            .level(LogLevel.ALL)
            .baseInput(FolderMultiInput(decompileSource.asPath()))
            .patchesInput(FolderMultiInput(patchDirectory.asPath()))
            .patchedOutput(SafeFolderMultiOutput(javaSource.asPath(), decompileSource.asPath()))
            .rejectsOutput(FolderMultiOutput(failedOutput.asPath()))
            .minFuzz(minFuzz.get())
            .mode(PatchMode.FUZZY)
            .build()

        val result = patcher.operate()
        logger.lifecycle("Applied ${result.summary?.changedFiles} patches")
    }
}
