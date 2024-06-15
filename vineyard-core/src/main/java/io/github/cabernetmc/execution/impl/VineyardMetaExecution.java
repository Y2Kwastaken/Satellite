package io.github.cabernetmc.execution.impl;

import io.github.cabernetmc.execution.ExecutionError;
import io.github.cabernetmc.execution.VineyardExecutionSettings;
import io.github.cabernetmc.meta.MinecraftVersion;
import io.github.cabernetmc.util.Result;
import io.github.cabernetmc.util.Result.Failure;
import io.github.cabernetmc.util.Result.Success;
import io.github.cabernetmc.util.Results;
import io.github.cabernetmc.util.VineyardConstants;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;

import static io.github.cabernetmc.util.Result.failure;
import static io.github.cabernetmc.util.Result.success;

/**
 * Grabs all metadata from Mojang's piston data api and is a pre-requisite for all other executions in some indirect
 * way
 */
public class VineyardMetaExecution implements Results<MinecraftVersion, IllegalArgumentException> {

    private final VineyardExecutionSettings settings;

    /**
     * Creates a new execution
     *
     * @param settings the settings
     */
    public VineyardMetaExecution(@NotNull final VineyardExecutionSettings settings) {
        this.settings = settings;
    }

    @Override
    public Result<MinecraftVersion, IllegalArgumentException> run() {
        final Result<MinecraftVersion, IllegalArgumentException> result;
        try (final HttpClient client = HttpClient.newHttpClient()) {
            result = new MinecraftVersion.Builder()
                    .targetVersion(settings.minecraftVersion())
                    .pistonManifestUrl(VineyardConstants.PISTON_META_LINK)
                    .build(client);
        }

        switch (result) {
            case Success<MinecraftVersion, ?> success -> {
                settings.whenDebug(VineyardConstants.LOG_RESULT_META_SUCCESS.formatted(settings.minecraftVersion()));
                return success(success.result);
            }
            case Failure<?, IllegalArgumentException> failure -> {
                settings.whenDebug(VineyardConstants.LOG_RESULT_META_FAILURE.formatted(settings.minecraftVersion()));
                return failure(failure.exception);
            }
            default -> throw new ExecutionError(VineyardConstants.LOG_RESULT_DEFAULTED);
        }
    }
}
