package com.github.cabernetmc.execution;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public record VineyardExecutionSettings(boolean debug, boolean ignoreCaches, @NotNull Path workingDirectory,
                                        @NotNull String version) {

    public static class Builder {

        private boolean debug;
        private boolean ignoreCaches;

        private Path workingDirectory;

        private String version;

        public Builder() {
        }

        public Builder debug(final boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder ignoreCaches(final boolean ignoreCaches) {
            this.ignoreCaches = ignoreCaches;
            return this;
        }

        public Builder workingDirectory(@NotNull final Path path) {
            this.workingDirectory = Objects.requireNonNull(path);
            return this;
        }

        public Builder version(@NotNull final String version) {
            this.version = Objects.requireNonNull(version);
            return this;
        }

        public VineyardExecutionSettings build() {
            if (!verifyInputs()) {
                throw new IllegalStateException("Not all required fields have been set");
            }

            return new VineyardExecutionSettings(
                    debug,
                    ignoreCaches,
                    workingDirectory,
                    version
            );
        }

        private boolean verifyInputs() {
            return workingDirectory != null && version != null;
        }
    }

}
