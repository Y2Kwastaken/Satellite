package io.github.cabernetmc.execution;

import io.github.cabernetmc.execution.step.VineyardDecompileExecution;
import io.github.cabernetmc.execution.step.VineyardDownloadExecution;
import io.github.cabernetmc.execution.step.VineyardExtractExecution;
import io.github.cabernetmc.execution.step.VineyardMetaExecution;
import io.github.cabernetmc.execution.step.VineyardRemapExecution;
import io.github.cabernetmc.meta.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class used to streamline creation of different VineyardExecutions
 */
public final class VineyardExecutionFactory {

    private final VineyardExecutionSettings settings;

    /**
     * Creates a new VineyardExecutionFactory
     *
     * @param settings settings to apply to the execution factory
     */
    public VineyardExecutionFactory(@NotNull final VineyardExecutionSettings settings) {
        this.settings = settings;
    }

    /**
     * Creates a meta execution
     *
     * @return the meta execution
     */
    public VineyardMetaExecution createMetaExecution() {
        return new VineyardMetaExecution(this.settings);
    }

    /**
     * Creates a download execution
     *
     * @param version the MinecraftVersion metadata
     * @return the download execution
     */
    public VineyardDownloadExecution createDownloadExecution(@NotNull final MinecraftVersion version) {
        return new VineyardDownloadExecution(this.settings, version);
    }

    /**
     * Creates an extraction execution
     *
     * @return the extraction execution
     */
    public VineyardExtractExecution createExtractExecution() {
        return new VineyardExtractExecution(this.settings);
    }

    /**
     * Creates a remap execution
     *
     * @return the remap execution
     */
    public VineyardRemapExecution createRemapExecution() {
        return new VineyardRemapExecution(this.settings);
    }

    /**
     * Creates a decompile execution
     *
     * @param version the MinecraftVersion metadata
     * @return the decompile execution
     */
    public VineyardDecompileExecution createDecompileExecution(@NotNull final MinecraftVersion version) {
        return new VineyardDecompileExecution(this.settings, version);
    }

}
