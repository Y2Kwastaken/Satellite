package io.github.orbitemc

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import java.io.File
import java.nio.file.Path

fun RegularFileProperty.asPath(): Path {
    return this.get().asFile.toPath()
}

fun RegularFileProperty.asFile(): File {
    return this.get().asFile
}

fun DirectoryProperty.asPath(): Path {
    return this.get().asFile.toPath()
}

fun DirectoryProperty.asFile(): File {
    return this.get().asFile
}
