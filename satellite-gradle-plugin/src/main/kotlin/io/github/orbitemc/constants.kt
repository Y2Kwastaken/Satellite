package io.github.kryptidemc

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

const val VINEYARD = "vineyard"
const val DOWNLOAD_MINECRAFT_META_TASK = "downloadGameMeta"

const val VERSION_CACHE_FOLDER = "version-meta-cache"

fun getVersionMetaCache(path: Path, minecraftVersion: String): Path {
    if (path.notExists()) path.createDirectories()
    return path.resolve(VERSION_CACHE_FOLDER).resolve("$minecraftVersion.json")
}
