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

    /**
     * Builder with sensible defaults for {@link VineyardExecutionSettings}
     */
    public static final class Builder {

        private boolean debug = false;
        private Consumer<String> debugHandler = null;
        private boolean ignoreCaches = false;
        private Path workingDirectory = Path.of("vineyard-work");

        /**
         * Enables the debug feature for the builder
         *
         * @return this builder
         */
        public Builder debug(@NotNull final Consumer<String> debugHandler) {
            this.debug = true;
            this.debugHandler = debugHandler;
            return this;
        }

        /**
         * Ignores caches
         *
         * @return this builder
         */
        public Builder ignoreCaches() {
            this.ignoreCaches = true;
            return this;
        }

        /**
         * Sets the working directory for the builder
         *
         * @param workingDirectory the working directory
         * @return this builder
         */
        public Builder workingDirectory(@NotNull final Path workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        /**
         * Builds the VineyardExecutionSettings
         *
         * @param minecraftVersion the minecraft version
         * @return the newly created execution settings
         */
        public VineyardExecutionSettings build(@NotNull final String minecraftVersion) {
            assert minecraftVersion != null;
            if (debug) assert this.debugHandler != null;

            return new VineyardExecutionSettings(this.debug, this.debugHandler, this.ignoreCaches, this.workingDirectory, minecraftVersion);
        }

    }

}
