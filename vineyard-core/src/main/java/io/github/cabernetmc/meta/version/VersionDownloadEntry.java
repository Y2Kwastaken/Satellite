package io.github.cabernetmc.meta.version;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Represents an entry within the download entry of a version
 *
 * @param sha1 the sha1 of the download link
 * @param size the size of the download
 * @param url  the url the download file is at
 * @since 1.0.0-SNAPSHOT
 */
public record VersionDownloadEntry(@NotNull String sha1, int size, @NotNull URI url) {

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
