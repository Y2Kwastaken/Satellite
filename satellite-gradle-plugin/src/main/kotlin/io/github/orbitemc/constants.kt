package io.github.orbitemc

import java.nio.file.Path

const val SATELLITE = "satellite"

const val SATELLITE_CATEGORY = SATELLITE
const val SATELLITE_ASSEMBLE_PROJECT_TASK = "assemble-project"
const val SATELLITE_SETUP_TASK = "setup"
const val SATELLITE_BUILD_PATCHES_TASK = "buildPatches"
const val SATELLITE_APPLY_PATCHES_TASK = "applyPatches"
const val SATELLITE_APPLY_PATCHES_FUZZY_TASK = "applyPatchesFuzzy"

const val SATELLITE_ASSEMBLE_CATEGORY = "$SATELLITE-assemble"
const val SATELLITE_GENERATE_VERSIONS_LIST_TASK = "generateVersionsList"
const val SATELLITE_GENERATE_LIBRARIES_LIST_TASK = "generateLibrariesList"
const val SATELLITE_BUILD_CHECKSUMS_TASK = "buildChecksums"
const val SATELLITE_BUILD_TASK = "assembleBootstrap"
const val SATELLITE_BUILD_JAR_TASK = "assembleJar"

const val SATELLITE_SETUP_CATEGORY = "$SATELLITE-setup"
const val DOWNLOAD_MINECRAFT_META_TASK = "downloadMinecraftMeta"
const val DOWNLOAD_MINECRAFT_BOOTSTRAP_JAR_TASK = "downloadMinecraftBootstrap"
const val DOWNLOAD_MINECRAFT_MAPPINGS_TASK = "downloadMinecraftMappings"
const val DOWNLOAD_DECOMPILER_TASK = "downloadDecompiler"
const val EXTRACT_MINECRAFT_BOOTSTRAP_JAR_TASK = "extractMinecraftBootstrap"
const val CONVERT_MINECRAFT_MAPPINGS_TASK = "convertMinecraftMappings"
const val REMAP_MINECRAFT_JAR_TASK = "remapMinecraftJar"
const val UNZIP_REMAP_JAR_TASK = "unzipRemapJar"
const val DECOMPILE_REMAP_CLASSES_TASK = "decompileRemapClasses"
const val COPY_JAVA_MINECRAFT_FILES_TASK = "copyJavaMinecraftFiles"

const val META_CACHE = "meta-cache"
const val BOOTSTRAP_CACHE = "bootstrap-cache"
const val BOOTSTRAP_UNZIP = "$BOOTSTRAP_CACHE/extract-%s"
const val MAPPING_CACHE = "mapping-cache"
const val REMAP_CACHE = "remap-cache"
const val DECOMPILE_CACHE = "decompile-cache"
const val DECOMPILE_CLASS_CACHE = "$DECOMPILE_CACHE/classes"
const val DECOMPILE_JAVA_CACHE = "$DECOMPILE_CACHE/java"

const val META_FILE = "meta.json"
const val SERVER_BOOTSTRAP_FILE = "minecraft-bootstrap.jar"
const val PROGUARD_MAPPINGS_FILE = "mappings.proguard"
const val TINY_MAPPINGS_FILE = "mappings.tiny"
const val SERVER_OBFUSCATED_FILE = "server-obfuscated.jar"
const val LIBRARIES_LIST_FILE = "libraries.list"
const val SERVER_REMAPPED_FILE = "server-remapped.jar"
const val CLASSES_FOLDER = "classes"
const val JAVA_FOLDER = "java"

const val BOOTSTRAP_JAR_META_LOC = "META-INF/versions/%s/server-%s.jar"
const val LIBRARIES_LIST_META_LOC = "META-INF/libraries.list"

fun getVersionFolder(path: Path, minecraftVersion: String): Path {
    return path.resolve(minecraftVersion)
}

fun getMetaFile(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(META_FILE).makeParents()
}

fun getBootstrapJar(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(SERVER_BOOTSTRAP_FILE).makeParents()
}

fun getProguardMappings(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(PROGUARD_MAPPINGS_FILE).makeParents()
}

fun getTinyMappings(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(TINY_MAPPINGS_FILE).makeParents()
}

fun getObfuscatedServer(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(SERVER_OBFUSCATED_FILE).makeParents()
}

fun getLibrariesList(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(LIBRARIES_LIST_FILE).makeParents()
}

fun getRemappedServer(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(SERVER_REMAPPED_FILE).makeParents()
}

fun getClassesFolder(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(CLASSES_FOLDER).makeParents()
}

fun getJavaFolder(path: Path, minecraftVersion: String): Path {
    return getVersionFolder(path, minecraftVersion).resolve(JAVA_FOLDER).makeParents()
}

