package com.github.cabernetmc.execution.impl;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class VineyardExtractExecution extends AbstractVineyardExecution {

    private static final String SERVER_EXTRACTED = "server/server-obfuscated.jar";
    private static final String LIBRARIES_EXTRACTED = "server/server-libraries.list";

    private Path server;
    private Path libraries;

    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public VineyardExtractExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        super(settings, requestHelper);
        this.dependencyClasses.add(VineyardDownloadExecution.class);
    }

    @Override
    public void execute() {
        final var downloadExecution = (VineyardDownloadExecution) dependencies.get(VineyardDownloadExecution.class);
        final var bootstrap = downloadExecution.serverBootstrap();
        final var work = settings.workingDirectory();
        final var extractedServer = work.resolve(SERVER_EXTRACTED);
        final var extractedLibraries = work.resolve(LIBRARIES_EXTRACTED);

        if (settings.ignoreCaches() || Files.notExists(extractedServer)) {
            if (settings.debug()) {
                System.out.printf("Extracting the server-%s.jar%n", settings.version());
            }
            VineyardUtils.jarExtract(bootstrap, extractedServer, "META-INF/versions/%s/server-%s.jar".formatted(settings.version(), settings.version()));
        }
        this.server = extractedServer;

        if (settings.ignoreCaches() || Files.notExists(extractedLibraries)) {
            if (settings.debug()) {
                System.out.println("Extracting libraries.list");
            }
            VineyardUtils.jarExtract(bootstrap, extractedLibraries, "META-INF/libraries.list");
        }
        this.libraries = extractedLibraries;
    }

    @Override
    public void verifyExecution() {
        assert this.server != null && this.libraries != null;
        assert Files.exists(this.server) && Files.exists(this.libraries);
    }

    @NotNull
    public Path server() {
        return this.server;
    }

    @NotNull
    public Path libraries() {
        return this.libraries;
    }
}
