package io.github.cabernetmc.execution;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Settings for execution when using any execution step or provides a default for {@link VineyardExecutionFactory} for
 * default settings
 *
 * @param debug            whether or not to enable debug logging
 * @param debugHandler     a function that is executed on debug logging being enabled
 * @param ignoreCaches     whether or not to ignore current caching
 * @param workingDirectory the working directory to run all tasks around
 * @param minecraftVersion the minecraft version to run all executions for
 */
public record VineyardExecutionSettings(boolean debug, @NotNull Consumer<String> debugHandler, boolean ignoreCaches,
                                        @NotNull Path workingDirectory, @NotNull String minecraftVersion) {

    /**
     * Executes the {@link #debugHandler()} given {@link #debug()} mode is true and a log is provided
     *
     * @param log the log to run through the {@link #debugHandler()}
     */
    public void whenDebug(@NotNull final String log) {
        if (debug) {
            debugHandler.accept(Objects.requireNonNull(log));
        }
    }

}
