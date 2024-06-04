package com.github.cabernetmc.step;

import com.github.cabernetmc.meta.version.VersionData;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class VineyardDecompileStep implements VineyardStep {

    public static final String DECOMPILE = "decompile/decompile-%s";

    private final boolean debug;
    private final Path work;
    private final VersionData data;

    public VineyardDecompileStep(final boolean debug, @NotNull final Path work, @NotNull final VersionData data) {
        this.debug = debug;
        this.work = work;
        this.data = data;
    }

    @Override
    public void run() {
        final var decompilePath = work.resolve(DECOMPILE.formatted(data.downloadEntries().get("server").sha1()));
        try {
            if (Files.notExists(decompilePath.getParent())) {
                Files.createDirectories(decompilePath.getParent());
            }

            if (Files.notExists(decompilePath.resolve("classes"))) {
                Files.createDirectories(decompilePath.resolve("classes"));
                VineyardUtils.unzip(work.resolve(VineyardRemapStep.REMAP_OUTPUT), decompilePath.resolve("classes"), (s) -> true);
            }

            new ProcessBuilder()
                    .command("java", "-jar", work.resolve(VineyardDownloadStep.VINE_FLOWER).toString(), "-dgs=1", "-hdc=0", "-rbr=0", "-asc=1", "-udc=0", decompilePath.resolve("classes").toString(), decompilePath.resolve("java").toString())
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void verifyCompleted() {
    }
}
