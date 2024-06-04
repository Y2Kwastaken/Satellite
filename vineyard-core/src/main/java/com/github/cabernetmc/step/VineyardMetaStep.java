package com.github.cabernetmc.step;

import com.github.cabernetmc.meta.MinecraftVersion;
import com.github.cabernetmc.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;

import static com.github.cabernetmc.util.Result.*;

/**
 * A VineyardStep which gathers metadata upon a version
 *
 * @since 1.0.0-SNAPSHOT
 */
public class VineyardMetaStep implements VineyardStep {

    private final boolean debug;
    private final String version;
    private final URI pistonMetaLink;

    public VineyardMetaStep(final boolean debug, @NotNull final String version, @NotNull final URI pistonMetaLink) {
        this.debug = debug;
        this.version = version;
        this.pistonMetaLink = pistonMetaLink;
    }

    /**
     * The MinecraftVersion meta data required. This field is null prior to running {@link #run()}
     */
    @Nullable
    public MinecraftVersion meta;

    @Override
    public void run() {
        Result<MinecraftVersion, IllegalArgumentException> result;
        try (final HttpClient client = HttpClient.newHttpClient()) {
            result = new MinecraftVersion.Builder()
                    .targetVersion(this.version)
                    .pistonManifestUrl(this.pistonMetaLink)
                    .build(client);
        }

        switch (result) {
            case Success<MinecraftVersion, IllegalArgumentException> success -> {
                this.meta = success.result;
                if (debug) {
                    System.out.println("Received all metadata");
                }
            }
            case Failure<MinecraftVersion, IllegalArgumentException> failure -> {
                if (debug) {
                    System.out.println("Failed to receive all metadata");
                }
                System.err.println(failure.exception.getMessage());
            }
            default -> {
            }
        }
    }

    @Override
    public void verifyCompleted() {
        assert meta != null;
    }
}
