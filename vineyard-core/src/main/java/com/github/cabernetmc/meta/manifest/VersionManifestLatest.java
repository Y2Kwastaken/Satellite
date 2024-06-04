package com.github.cabernetmc.meta.manifest;

import org.jetbrains.annotations.NotNull;

/**
 * Contains latest versions for each release type within the version Manifest
 *
 * @param release  the latest release version
 * @param snapshot the latest snapshot version
 * @since 1.0.0-SNAPSHOT
 */
public record VersionManifestLatest(@NotNull String release, @NotNull String snapshot) {
}
