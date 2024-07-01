package io.github.cabernetmc.execution.impl;

import io.github.cabernetmc.execution.VineyardExecutionSettings;
import io.github.cabernetmc.meta.MinecraftVersion;
import io.github.cabernetmc.util.Result;
import io.github.cabernetmc.util.Results;
import io.github.cabernetmc.util.VineyardConstants;
import io.github.cabernetmc.util.VineyardUtils;
import io.github.cabernetmc.meta.version.VersionData;
import io.github.cabernetmc.meta.version.VersionDownloadEntry;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.cabernetmc.util.Result.failure;
import static io.github.cabernetmc.util.Result.success;

/**
 * An execution which downloads relevant downloads from the web for setup
 */
public class VineyardDownloadExecution implements Results<Boolean, IllegalStateException> {

    private final VineyardExecutionSettings settings;
    private final MinecraftVersion version;

    /**
     * Creates a new execution
     *
     * @param settings the settings
     * @param version  the version metadata
     */
    public VineyardDownloadExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final MinecraftVersion version) {
        this.settings = settings;
        this.version = version;
    }

    @Override
    public Result<Boolean, IllegalStateException> run() {
        final Path bootstrap = settings.workingDirectory().resolve(VineyardConstants.PATH_BOOTSTRAP_DESTINATION);
        if (settings.ignoreCaches() || Files.notExists(bootstrap)) {
            final VersionDownloadEntry vde = version.data().downloadEntries().get(VersionData.SERVER);
            settings.whenDebug(VineyardConstants.LOG_DOWNLOAD_BOOTSTRAP.formatted(settings.minecraftVersion(), vde.url(), vde.sha1()));
            if (!VineyardUtils.download(vde.url(), bootstrap, true)) {
                return failure(new IllegalStateException(VineyardConstants.LOG_DOWNLOAD_BOOTSTRAP_FAILURE));
            }
        }

        final Path mappings = settings.workingDirectory().resolve(VineyardConstants.PATH_MAPPINGS_DESTINATION);
        if (settings.ignoreCaches() || Files.notExists(mappings)) {
            final VersionDownloadEntry vde = version.data().downloadEntries().get(VersionData.SERVER_MAPPINGS);
            settings.whenDebug(VineyardConstants.LOG_DOWNLOAD_MAPPINGS.formatted(settings.minecraftVersion(), vde.sha1(), vde.sha1()));
            if (!VineyardUtils.download(vde.url(), mappings, true)) {
                return failure(new IllegalStateException(VineyardConstants.LOG_DOWNLOAD_MAPPINGS_FAILURE));
            }
        }

        final Path vineflower = settings.workingDirectory().resolve(VineyardConstants.PATH_VINEFLOWER_DESTINATION);
        if (settings.ignoreCaches() || Files.notExists(vineflower)) {
            settings.whenDebug(VineyardConstants.LOG_DOWNLOAD_VINEFLOWER.formatted(VineyardConstants.VINEFLOWER_LINK));
            if (!VineyardUtils.download(VineyardConstants.VINEFLOWER_LINK, vineflower, true)) {
                return failure(new IllegalStateException(VineyardConstants.LOG_DOWNLOAD_VINEFLOWER_FAILURE));
            }
        }

        return success(true);
    }
}
