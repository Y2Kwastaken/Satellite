package com.github.cabernetmc.meta;

import com.github.cabernetmc.meta.manifest.VersionManifest;
import com.github.cabernetmc.meta.manifest.VersionManifestEntry;
import com.github.cabernetmc.meta.version.VersionData;
import com.github.cabernetmc.util.Result;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import static com.github.cabernetmc.util.Result.failure;
import static com.github.cabernetmc.util.Result.success;

/**
 * Represents a MinecraftVersion and contains relevant information
 *
 * @param version the version manifest data
 * @param data    the data required to continuer
 * @since 1.0.0-SNAPSHOT
 */
public record MinecraftVersion(@NotNull VersionManifestEntry version, @NotNull VersionData data) {

    /**
     * Builds a {@link MinecraftVersion}
     */
    public static class Builder {

        private URI pistonManifestUrl;
        private String version;

        /**
         * Sets the target piston URL
         *
         * @param uri the piston url
         * @return this
         */
        public Builder pistonManifestUrl(@NotNull final URI uri) {
            this.pistonManifestUrl = Objects.requireNonNull(uri);
            return this;
        }

        /**
         * Sets the target version to get with this builder
         *
         * @param version the version
         * @return this
         */
        public Builder targetVersion(@NotNull final String version) {
            this.version = version;
            return this;
        }

        /**
         * Builds the builder into a MinecraftVersion record
         *
         * @param client the HTTPClient to use
         * @return the result
         * @since 1.0.0-SNAPSHOT
         */
        public Result<MinecraftVersion, IllegalArgumentException> build(@NotNull final HttpClient client) {
            if (pistonManifestUrl == null) {
                return failure(new IllegalArgumentException("The piston manifest url must not be null"));
            }

            HttpRequest request = HttpRequest.newBuilder().uri(pistonManifestUrl).GET().build();
            String body;
            try {
                body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                return failure(new IllegalArgumentException(e));
            }

            final VersionManifest manifest = VineyardUtils.GSON.fromJson(body, VersionManifest.class);

            String targetVersion;
            if (version == null || version.equalsIgnoreCase("latest")) {
                targetVersion = manifest.latest().release();
            } else if (version.equalsIgnoreCase("snapshot")) {
                targetVersion = manifest.latest().snapshot();
            } else {
                targetVersion = version;
            }

            VersionManifestEntry versionEntry = null;
            for (final VersionManifestEntry entry : manifest.entries()) {
                if (entry.id().equalsIgnoreCase(targetVersion)) {
                    if (!entry.isValid()) {
                        return failure(new IllegalArgumentException("While the entry for version %s exists the sha values do not match!".formatted(targetVersion)));
                    }
                    versionEntry = entry;
                    break;
                }
            }

            if (versionEntry == null) {
                return failure(new IllegalArgumentException("no valid manifest entry for version %s was found".formatted(targetVersion)));
            }

            request = HttpRequest.newBuilder().uri(versionEntry.url()).GET().build();
            try {
                body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                return failure(new IllegalArgumentException(e));
            }

            final VersionData data = VineyardUtils.GSON.fromJson(body, VersionData.class);

            return success(new MinecraftVersion(versionEntry, data));
        }

    }
}
