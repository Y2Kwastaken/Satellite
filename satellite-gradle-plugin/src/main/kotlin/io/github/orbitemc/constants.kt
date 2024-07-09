package io.github.orbitemc

import java.net.URI
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.notExists

const val SATELLITE = "satellite"

const val SATELLITE_CATEGORY = SATELLITE
const val SATELLITE_SETUP_TASK = "setup"
const val SATELLITE_BUILD_PATCHES_TASK = "build-patches"
const val SATELLITE_APPLY_PATCHES_TASK = "apply-patches"

const val SATELLITE_SETUP_CATEGORY = "$SATELLITE-setup"
const val DOWNLOAD_MINECRAFT_META_TASK = "download-minecraft-meta"
const val DOWNLOAD_MINECRAFT_BOOTSTRAP_JAR_TASK = "download-minecraft-bootstrap"
const val DOWNLOAD_MINECRAFT_MAPPINGS_TASK = "download-minecraft-mappings"
const val DOWNLOAD_VINEFLOWER_TASK = "download-vineflower"
const val EXTRACT_MINECRAFT_BOOTSTRAP_JAR_TASK = "extract-minecraft-bootstrap"
const val CONVERT_MINECRAFT_MAPPINGS_TASK = "convert-minecraft-mappings"
const val REMAP_MINECRAFT_JAR_TASK = "remap-minecraft-jar"
const val UNZIP_REMAP_JAR_TASK = "unzip-remap-jar"
const val DECOMPILE_REMAP_CLASSES_TASK = "decompile-remap-classes"
const val COPY_JAVA_MINECRAFT_FILES_TASK = "copy-java-minecraft-files"

const val META_CACHE = "meta-cache"
const val BOOTSTRAP_CACHE = "bootstrap-cache"
const val BOOTSTRAP_UNZIP = "$BOOTSTRAP_CACHE/extract-%s"
const val MAPPING_CACHE = "mapping-cache"
const val REMAP_CACHE = "remap-cache"
const val DECOMPILE_CACHE = "decompile-cache"
const val DECOMPILE_CLASS_CACHE = "$DECOMPILE_CACHE/classes"
const val DECOMPILE_JAVA_CACHE = "$DECOMPILE_CACHE/java"

const val BOOTSTRAP_JAR_META_LOC = "META-INF/versions/%s/server-%s.jar"
const val LIBRARIES_LIST_META_LOC = "META-INF/libraries.list"

const val VINEFLOWER_LINK = "https://github.com/Vineflower/vineflower/releases/download/%s/vineflower-%s.jar"

fun getVineFlowerLink(version: String): URI {
    return URI.create(VINEFLOWER_LINK.format(version, version))
}

fun getVersionMetaFile(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(META_CACHE).resolve("$minecraftVersion.json")
}

fun getVersionBootstrapFile(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(BOOTSTRAP_CACHE).resolve("$minecraftVersion-bootstrap.jar")
}

fun getVersionProguardMappingFile(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(MAPPING_CACHE).resolve("proguard-$minecraftVersion.txt")
}

fun getVersionTinyMappingFile(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(MAPPING_CACHE).resolve("tiny-$minecraftVersion.tiny")
}

fun getVersionBootstrapExtractDir(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(BOOTSTRAP_UNZIP.format(minecraftVersion))
}

fun getVersionServerObfuscatedFile(path: Path, minecraftVersion: String): Path {
    return getVersionBootstrapExtractDir(path, minecraftVersion).resolve("$minecraftVersion-obfuscated.jar")
}

fun getVersionLibrariesFile(path: Path, minecraftVersion: String): Path {
    return getVersionBootstrapExtractDir(path, minecraftVersion).resolve("libraries.list")
}

fun getVersionServerRemapFile(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(REMAP_CACHE).resolve("$minecraftVersion-remapped.jar")
}

fun getVersionDecompileClassDir(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(DECOMPILE_CLASS_CACHE).resolve(minecraftVersion)
}

fun getVersionDecompileJavaDir(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(DECOMPILE_JAVA_CACHE).resolve(minecraftVersion)
}

fun isVersionDecompileJavaDir(path: Path, minecraftVersion: String): Boolean {
    if (path.notExists()) path.createDirectories()
    return path.resolve(DECOMPILE_JAVA_CACHE).resolve(minecraftVersion).exists()
}

