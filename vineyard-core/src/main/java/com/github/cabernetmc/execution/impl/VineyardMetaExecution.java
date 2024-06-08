package com.github.cabernetmc.execution.impl;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import com.github.cabernetmc.meta.MinecraftVersion;
import com.github.cabernetmc.util.Result;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;

public class VineyardMetaExecution extends AbstractVineyardExecution {

    private MinecraftVersion versionMeta;

    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public VineyardMetaExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        super(settings, requestHelper);
    }

    @Override
    public void execute() {
        Result<MinecraftVersion, IllegalArgumentException> result;
        try (final HttpClient client = HttpClient.newHttpClient()) {
            result = new MinecraftVersion.Builder()
                    .targetVersion(settings.version())
                    .pistonManifestUrl(VineyardUtils.PISTON_META_LINK)
                    .build(client);
        }

        switch (result) {
            case Result.Success<MinecraftVersion, IllegalArgumentException> success -> {
                this.versionMeta = success.result;
                if (settings.debug()) {
                    System.out.println("Received all metadata");
                }
            }
            case Result.Failure<MinecraftVersion, IllegalArgumentException> failure -> {
                if (settings.debug()) {
                    System.out.println("Failed to receive all metadata");
                }
                System.err.println(failure.exception.getMessage());
            }
            default -> {
            }
        }


    }

    @Override
    public void verifyExecution() {
        assert versionMeta != null;
    }

    @NotNull
    public MinecraftVersion versionMeta() {
        return this.versionMeta;
    }
}
