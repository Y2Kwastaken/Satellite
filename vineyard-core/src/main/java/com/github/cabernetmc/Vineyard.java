package com.github.cabernetmc;

import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.VineyardExecutor;
import com.github.cabernetmc.execution.impl.VineyardDecompileExecution;
import com.github.cabernetmc.execution.impl.VineyardDownloadExecution;
import com.github.cabernetmc.execution.impl.VineyardExtractExecution;
import com.github.cabernetmc.execution.impl.VineyardMetaExecution;
import com.github.cabernetmc.execution.impl.VineyardRemapperExecution;
import com.github.cabernetmc.execution.utility.SimpleVineyardExecutionRequestHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The main Vineyard runner used to run the Vineyard tooling
 *
 * @since 1.0.0-SNAPSHOT
 */
public final class Vineyard {

    /**
     * Creates a defaultExecution VineyardExecutor with standard settings and executions
     *
     * @param version the version to execute for
     * @return VineyardExecutor
     */
    public static VineyardExecutor defaultExecution(@NotNull final String version) {
        return defaultExecutionBuilder(version).build();
    }

    /**
     * Creates a VineyardExecutor builder with default execution parameters
     *
     * @return the VineyardExecutor builder
     */
    public static VineyardExecutor.Builder defaultExecutionBuilder(@NotNull final String version) {
        return standardExecutionBuilder(version)
                .execution(VineyardMetaExecution.class)
                .execution(VineyardDownloadExecution.class)
                .execution(VineyardExtractExecution.class)
                .execution(VineyardRemapperExecution.class)
                .execution(VineyardDecompileExecution.class);
    }

    /**
     * Creates a VineyardExecutor Builder with standard settings
     *
     * @return the new execution
     */
    public static VineyardExecutor.Builder standardExecutionBuilder(@NotNull final String version) {
        return new VineyardExecutor.Builder()
                .settings(new VineyardExecutionSettings.Builder()
                        .workingDirectory(Path.of("vineyard-build"))
                        .version(version)
                        .build()
                ).requestHelper(new SimpleVineyardExecutionRequestHelper());
    }

    /**
     * Creates a new Execution Builder for vineyard
     *
     * @return the new execution
     */
    public static VineyardExecutor.Builder emptyExecutionBuilder() {
        return new VineyardExecutor.Builder();
    }

}
