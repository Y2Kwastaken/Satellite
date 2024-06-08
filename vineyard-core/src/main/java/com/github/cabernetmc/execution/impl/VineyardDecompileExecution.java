package com.github.cabernetmc.execution.impl;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class VineyardDecompileExecution extends AbstractVineyardExecution {

    private static final String DECOMPILE = "decompile/decompile-%s";


    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public VineyardDecompileExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        super(settings, requestHelper);
        this.dependencyClasses.addAll(Set.of(VineyardMetaExecution.class, VineyardDownloadExecution.class, VineyardRemapperExecution.class));
    }

    @Override
    public void execute() {
        final var meta = ((VineyardMetaExecution) dependencies.get(VineyardMetaExecution.class)).versionMeta();
        final var obfuscatedServer = ((VineyardRemapperExecution) dependencies.get(VineyardRemapperExecution.class)).server();
        final var vineFlower = ((VineyardDownloadExecution) dependencies.get(VineyardDownloadExecution.class)).vineflower();
        final var work = settings.workingDirectory();

        final var decompilePath = work.resolve(DECOMPILE.formatted(meta.data().downloadEntries().get("server").sha1()));
        try {
            if (Files.notExists(decompilePath.getParent())) {
                Files.createDirectories(decompilePath.getParent());
            }

            if (Files.notExists(decompilePath.resolve("classes"))) {
                Files.createDirectories(decompilePath.resolve("classes"));
                VineyardUtils.unzip(obfuscatedServer, decompilePath.resolve("classes"), (s) -> true);
            }

            new ProcessBuilder()
                    .command("java", "-jar", vineFlower.toString(), "-dgs=1", "-hdc=0", "-rbr=0", "-asc=1", "-udc=0", decompilePath.resolve("classes").toString(), decompilePath.resolve("java").toString())
                    .inheritIO()
                    .start()
                    .waitFor();

            Files.createSymbolicLink(work.resolve(DECOMPILE.formatted("target")), work.resolve(DECOMPILE.formatted(meta.data().downloadEntries().get("server").sha1())));
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyExecution() {
    }
}
