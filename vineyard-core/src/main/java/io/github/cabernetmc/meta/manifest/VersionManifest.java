package io.github.cabernetmc.meta.manifest;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents the version manifest provided by
 * {@literal https://piston-meta.mojang.com/mc/game/version_manifest_v2.json}. Contains all information within the
 * manifest.
 *
 * @param latest  the latest version information
 * @param entries all version entries
 * @since 1.0.0-SNAPSHOT
 */
public record VersionManifest(@NotNull VersionManifestLatest latest, @NotNull List<VersionManifestEntry> entries) {
}
