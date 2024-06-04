package com.github.cabernetmc.step;

import com.github.cabernetmc.meta.version.VersionData;
import com.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class VineyardDownloadStep implements VineyardStep {

    public static final String SERVER_BOOTSTRAP = "server/server-bootstrap.jar";
    public static final String SERVER_MAPPINGS = "mappings/server-mappings.txt";
    public static final String VINE_FLOWER = "vineflower.jar";

    private final boolean debug;
    private final VersionData versionData;
    private final Path work;
    private final URI vineflowerUrl;
    private final boolean downloadServerIfExists;
    private final boolean downloadMappingsIfExists;
    private final boolean ignoreCaches;

    public VineyardDownloadStep(final boolean debug, @NotNull final VersionData versionData, @NotNull final Path work, @NotNull final URI vineflowerUrl, final boolean downloadServerIfExists, final boolean downloadMappingsIfExists, final boolean ignoreCaches) {
        this.debug = debug;
        this.versionData = versionData;
        this.work = work;
        this.vineflowerUrl = vineflowerUrl;
        this.downloadServerIfExists = downloadServerIfExists;
        this.downloadMappingsIfExists = downloadMappingsIfExists;
        this.ignoreCaches = ignoreCaches;
    }

    @Override
    public void run() {
        if (ignoreCaches || downloadServerIfExists || Files.notExists(work.resolve(SERVER_BOOTSTRAP))) {
            if (debug) {
                System.out.printf("Downloading server bootstrap to %s%n", work.resolve(SERVER_BOOTSTRAP));
            }
            VineyardUtils.download(versionData.downloadEntries().get(VersionData.SERVER).url(), work.resolve(SERVER_BOOTSTRAP), true);
        }

        if (ignoreCaches || downloadMappingsIfExists || Files.notExists(work.resolve(SERVER_MAPPINGS))) {
            if (debug) {
                System.out.printf("Downloading server mappings to %s%n", work.resolve(SERVER_MAPPINGS));
            }
            VineyardUtils.download(versionData.downloadEntries().get(VersionData.SERVER_MAPPINGS).url(), work.resolve(SERVER_MAPPINGS), true);
        }

        if (ignoreCaches || Files.notExists(work.resolve(VINE_FLOWER))) {
            if (debug) {
                System.out.printf("Downloading vineflower to %s%n", work.resolve(VINE_FLOWER));
            }
            VineyardUtils.download(vineflowerUrl, work.resolve(VINE_FLOWER), true);
        }
    }

    public void verifyCompleted() {
        assert Files.exists(work.resolve(SERVER_BOOTSTRAP));
        assert Files.exists(work.resolve(SERVER_MAPPINGS));
        assert Files.exists(work.resolve(VINE_FLOWER));
    }
}
