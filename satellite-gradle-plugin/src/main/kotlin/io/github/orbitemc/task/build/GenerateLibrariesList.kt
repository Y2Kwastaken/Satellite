package io.github.orbitemc.task.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateLibrariesList : DefaultTask() {

    @get:InputDirectory
    abstract val checksumDirectory: DirectoryProperty

    @get:OutputFile
    abstract val librariesList: RegularFileProperty

    @TaskAction
    fun execute() {
        val checksumDir = checksumDirectory.get().asFile
        val bundled = project.configurations.named("bundled").get()
        val libraries = librariesList.get().asFile
        val writtenLines = bundled.files.map { checksumDir.resolve("${it.name}.sha512").readText() }.toMutableList()
        libraries.writeText(writtenLines.joinToString("\n"))
    }

}
