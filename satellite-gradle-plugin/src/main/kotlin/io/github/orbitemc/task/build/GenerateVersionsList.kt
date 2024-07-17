package io.github.orbitemc.task.build

import io.github.orbitemc.asFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateVersionsList : DefaultTask() {

    @get:InputFile
    abstract val jarOutput: RegularFileProperty

    @get:InputDirectory
    abstract val checksumDirectory: DirectoryProperty

    @get:OutputFile
    abstract val versionsList: RegularFileProperty

    @TaskAction
    fun execute() {
        val jarFile = checksumDirectory.asFile.get().resolve("${jarOutput.asFile().name}.sha512")
        versionsList.asFile().writeText(jarFile.readText())
    }

}
