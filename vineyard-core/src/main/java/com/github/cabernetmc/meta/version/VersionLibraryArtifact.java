package com.github.cabernetmc.meta.version;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Represents a library artifact within a version
 *
 * @param path the path of the library within the classpath
 * @param sha1 the sha1 of the downloaded jar to check against the downloaded entry
 * @param size the expected size of the jar
 * @param url  the url to download the library from
 * @since 1.0.0-SNAPSHOT
 */
public record VersionLibraryArtifact(@NotNull String path, @NotNull String sha1, int size, @NotNull URI url) {
}
