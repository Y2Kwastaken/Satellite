package io.github.orbitemc

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

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

fun Path.makeParents(): Path {
    if (this.exists()) return this
    if (this.isDirectory() || this.parent.notExists()) this.parent.createDirectories()
    return this
}
