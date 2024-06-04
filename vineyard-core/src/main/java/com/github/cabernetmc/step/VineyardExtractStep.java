package com.github.cabernetmc.step;

import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class VineyardExtractStep implements VineyardStep {

    public static final String SERVER_EXTRACTED = "server/server-obfuscated.jar";
    public static final String LIBRARIES_EXTRACTED = "server/server-libraries.list";

    private final boolean debug;
    private final Path work;
    private final String version;
    private final boolean ignoreCaches;


    public VineyardExtractStep(final boolean debug, @NotNull final Path work, @NotNull final String version, final boolean ignoreCaches) {
        this.debug = debug;
        this.work = work;
        this.version = version;
        this.ignoreCaches = ignoreCaches;
    }

    @Override
    public void run() {
        final Path jar = work.resolve(VineyardDownloadStep.SERVER_BOOTSTRAP);
        final Path extractedServer = work.resolve(SERVER_EXTRACTED);
        final Path extractedLibraries = work.resolve(LIBRARIES_EXTRACTED);

        if (ignoreCaches || Files.notExists(extractedServer)) {
            if (debug) {
                System.out.printf("Extracting the server-%s.jar%n", version);
            }
            VineyardUtils.jarExtract(jar, extractedServer, "META-INF/versions/%s/server-%s.jar".formatted(version, version));
        }

        if (ignoreCaches || Files.notExists(extractedLibraries)) {
            if (debug) {
                System.out.println("Extracting libraries.list");
            }
            VineyardUtils.jarExtract(jar, extractedLibraries, "META-INF/libraries.list");
        }
    }

    @Override
    public void verifyCompleted() {
        assert Files.exists(work.resolve(SERVER_EXTRACTED));
        assert Files.exists(work.resolve(LIBRARIES_EXTRACTED));
    }
}
