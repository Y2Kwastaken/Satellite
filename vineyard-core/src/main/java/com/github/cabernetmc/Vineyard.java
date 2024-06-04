package com.github.cabernetmc;

import com.github.cabernetmc.meta.MinecraftVersion;
import com.github.cabernetmc.step.VineyardDecompileStep;
import com.github.cabernetmc.step.VineyardDownloadStep;
import com.github.cabernetmc.step.VineyardExtractStep;
import com.github.cabernetmc.step.VineyardMetaStep;
import com.github.cabernetmc.step.VineyardRemapStep;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.Path;

/**
 * The main Vineyard runner used to run the Vineyard tooling
 *
 * @since 1.0.0-SNAPSHOT
 */
public final class Vineyard {

    private final MinecraftVersion version;
    private final Path workingDirectory;

    private Vineyard(@NotNull final MinecraftVersion version, @NotNull final Path workingDirectory) {
        this.version = version;
        this.workingDirectory = workingDirectory;
    }

    @NotNull
    public MinecraftVersion getVersion() {
        return version;
    }

    @NotNull
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public static Builder newExecution() {
        return new Builder();
    }

    /**
     * Is the main builder for {@link Vineyard} builds a Vineyard which sets
     */
    public static final class Builder {

        private String version;
        private URI vineflowerUrl = VineyardUtils.VINEFLOWER_LINK;
        private Path work = Path.of("work");
        private boolean debug = false;
        private boolean downloadServerIfExists = false;
        private boolean downloadMappingsIfExist = false;
        private boolean ignoreCaches = false;

        private Builder() {
        }

        public Builder version(@NotNull final String version) {
            this.version = version;
            return this;
        }

        public Builder vineflowerUrl(@NotNull final URI url) {
            this.vineflowerUrl = url;
            return this;
        }

        public Builder work(@NotNull final Path work) {
            this.work = work;
            return this;
        }

        public Builder debug(final boolean flag) {
            this.debug = flag;
            return this;
        }

        public Builder downloadServerIfExists(final boolean flag) {
            this.downloadServerIfExists = flag;
            return this;
        }

        public Builder downloadMappingsIfExists(final boolean flag) {
            this.downloadMappingsIfExist = flag;
            return this;
        }

        public Builder ignoreCaches(final boolean flag) {
            this.ignoreCaches = flag;
            return this;
        }

        public Vineyard execute() {
            verifyBuild();
            final var metaStep = new VineyardMetaStep(this.debug, this.version, VineyardUtils.PISTON_META_LINK);
            metaStep.run();
            metaStep.verifyCompleted();

            final var downloadStep = new VineyardDownloadStep(this.debug, metaStep.meta.data(), this.work, vineflowerUrl, this.downloadServerIfExists, this.downloadMappingsIfExist, this.ignoreCaches);
            downloadStep.run();
            downloadStep.verifyCompleted();

            final var extractStep = new VineyardExtractStep(this.debug, this.work, this.version, this.ignoreCaches);
            extractStep.run();
            extractStep.verifyCompleted();

            final var remapStep = new VineyardRemapStep(this.debug, this.work, this.ignoreCaches);
            remapStep.run();
            remapStep.verifyCompleted();

            final var vineyardDecompileStep = new VineyardDecompileStep(this.debug, this.work, metaStep.meta.data());
            vineyardDecompileStep.run();
            vineyardDecompileStep.verifyCompleted();

            return new Vineyard(metaStep.meta, work);
        }

        private void verifyBuild() {
            if (this.version == null) {
                throw new IllegalArgumentException("The version field must not be null, and must be set to a valid minecraft version");
            }

            if (this.work == null) {
                throw new IllegalArgumentException("The version work must not be null, and must be set to a valid directory");
            }
        }
    }
}
