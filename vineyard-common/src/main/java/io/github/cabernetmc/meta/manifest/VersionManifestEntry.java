package io.github.cabernetmc.meta.manifest;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * An entry within  the version manifest
 *
 * @param id   the version title e.g. 1.20.4, 1.20.6, 24w21b
 * @param type the release type, either snapshot or release
 * @param url  the url to the version details
 * @param sha1 the sha1 in the url
 * @since 1.0.0-SNAPSHOT
 */
public record VersionManifestEntry(@NotNull String id, @NotNull String type, @NotNull URI url, @NotNull String sha1) {

    /**
     * Ensures the {@link #sha1()} and {@link #url()} have matching sha values
     *
     * @return true if the sha matches, otherwise false
     */
    public boolean isValid() {
        final String[] urlParts = url.getPath().split("/");
        return urlParts.length >= 3 && urlParts[3].equalsIgnoreCase(sha1);
    }
}
