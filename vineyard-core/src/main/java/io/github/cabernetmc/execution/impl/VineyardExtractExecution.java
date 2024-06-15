package io.github.cabernetmc.execution.impl;

import io.github.cabernetmc.execution.ExecutionError;
import io.github.cabernetmc.execution.VineyardExecutionSettings;
import io.github.cabernetmc.util.Result;
import io.github.cabernetmc.util.Result.Failure;
import io.github.cabernetmc.util.Result.Success;
import io.github.cabernetmc.util.Results;
import io.github.cabernetmc.util.VineyardConstants;
import io.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.cabernetmc.util.Result.success;

/**
 * An execution which is responsible for extracting important things from the minecraft bootstrap jar
 */
public class VineyardExtractExecution implements Results<Boolean, IllegalStateException> {

    private final VineyardExecutionSettings settings;

    /**
     * Creates a new execution
     *
     * @param settings the settings
     */
    public VineyardExtractExecution(@NotNull final VineyardExecutionSettings settings) {
        this.settings = settings;
    }

    @Override
    public Result<Boolean, IllegalStateException> run() {
        final Path bootstrap = settings.workingDirectory().resolve(VineyardConstants.PATH_BOOTSTRAP_DESTINATION);

        final Path obfuscatedServer = settings.workingDirectory().resolve(VineyardConstants.PATH_OBFUSCATED_SERVER_DESTINATION);
        if (settings.ignoreCaches() || Files.notExists(obfuscatedServer)) {
            settings.whenDebug(VineyardConstants.LOG_EXTRACT_OBFUSCATED_SERVER.formatted(obfuscatedServer));
            switch (VineyardUtils.jarExtract(bootstrap, obfuscatedServer, "META-INF/versions/%s/server-%s.jar".formatted(settings.minecraftVersion(), settings.minecraftVersion()))) {
                case Success<?, ?> ignored -> {
                }
                case Failure<?, IOException> failure -> {
                    throw new ExecutionError(failure.exception);
                }
                default -> throw new ExecutionError(VineyardConstants.LOG_RESULT_DEFAULTED);
            }
        }

        final Path librariesList = settings.workingDirectory().resolve(VineyardConstants.PATH_LIBRARIES_LIST_DESTINATION);
        if (settings.ignoreCaches() || Files.notExists(librariesList)) {
            settings.whenDebug(VineyardConstants.LOG_EXTRACT_LIBRARIES_LIST.formatted(librariesList));
            switch (VineyardUtils.jarExtract(bootstrap, librariesList, "META-INF/libraries.list")) {
                case Success<?, ?> ignored -> {
                }
                case Failure<?, IOException> failure -> {
                    throw new ExecutionError(failure.exception);
                }
                default -> throw new ExecutionError(VineyardConstants.LOG_RESULT_DEFAULTED);
            }
        }
        return success(true);
    }
}
