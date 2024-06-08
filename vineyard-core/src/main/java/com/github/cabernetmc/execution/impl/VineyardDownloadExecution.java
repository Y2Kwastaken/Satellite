package com.github.cabernetmc.execution.impl;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import com.github.cabernetmc.meta.version.VersionData;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class VineyardDownloadExecution extends AbstractVineyardExecution {

    private static final String SERVER_BOOTSTRAP = "server/server-bootstrap.jar";
    private static final String SERVER_MAPPINGS = "mappings/server-mappings.txt";
    private static final String VINE_FLOWER = "vineflower.jar";

    private Path serverBootstrap;
    private Path serverMappings;
    private Path vineflower;

    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public VineyardDownloadExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        super(settings, requestHelper);
        this.dependencyClasses.add(VineyardMetaExecution.class);
    }

    @Override
    public void execute() {
        final var meta = ((VineyardMetaExecution) dependencies.get(VineyardMetaExecution.class)).versionMeta();
        final var versionData = meta.data();
        final var work = settings.workingDirectory();

        if (settings.ignoreCaches() || Files.notExists(work.resolve(SERVER_BOOTSTRAP))) {
            if (settings.debug()) {
                System.out.printf("Downloading server bootstrap to %s%n", work.resolve(SERVER_BOOTSTRAP));
            }
            requestHelper.download(versionData.downloadEntries().get(VersionData.SERVER).url(), work.resolve(SERVER_BOOTSTRAP), true);
        }
        this.serverBootstrap = work.resolve(SERVER_BOOTSTRAP);

        if (settings.ignoreCaches() || Files.notExists(work.resolve(SERVER_MAPPINGS))) {
            if (settings.debug()) {
                System.out.printf("Downloading server mappings to %s%n", work.resolve(SERVER_MAPPINGS));
            }
            requestHelper.download(versionData.downloadEntries().get(VersionData.SERVER_MAPPINGS).url(), work.resolve(SERVER_MAPPINGS), true);
        }
        this.serverMappings = work.resolve(SERVER_MAPPINGS);

        if (settings.ignoreCaches() || Files.notExists(work.resolve(VINE_FLOWER))) {
            if (settings.debug()) {
                System.out.printf("Downloading vineflower to %s%n", work.resolve(VINE_FLOWER));
            }
            requestHelper.download(VineyardUtils.VINEFLOWER_LINK, work.resolve(VINE_FLOWER), true);
        }
        this.vineflower = work.resolve(VINE_FLOWER);
    }

    @Override
    public void verifyExecution() {
        assert this.vineflower != null && this.serverMappings != null && this.serverBootstrap != null;
        assert Files.exists(this.vineflower) && Files.exists(this.serverMappings) && Files.exists(this.serverBootstrap);
    }

    @NotNull
    public Path vineflower() {
        return this.vineflower;
    }

    @NotNull
    public Path serverMappings() {
        return this.serverMappings;
    }

    @NotNull
    public Path serverBootstrap() {
        return this.serverBootstrap;
    }
}
