package com.github.cabernetmc.meta.version;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a library within a version
 *
 * @param name     the dependency notation of the library
 * @param artifact the artifact information
 * @since 1.0.0-SNAPSHOT
 */
public record VersionLibrary(@NotNull String name, @NotNull VersionLibraryArtifact artifact) {
}
