package io.github.cabernetmc.execution.impl;

import io.github.cabernetmc.execution.VineyardExecutionSettings;
import io.github.cabernetmc.meta.MinecraftVersion;
import io.github.cabernetmc.meta.version.VersionData;
import io.github.cabernetmc.util.Result;
import io.github.cabernetmc.util.Results;
import io.github.cabernetmc.util.VineyardConstants;
import io.github.cabernetmc.util.VineyardUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import static io.github.cabernetmc.util.Result.failure;
import static io.github.cabernetmc.util.Result.success;

/**
 * An Execution that handles decompilation of the remapped jar from any provided source or more formally from
 * {@link VineyardRemapExecution}
 */
public class VineyardDecompileExecution implements Results<Boolean, IllegalStateException> {

    private final VineyardExecutionSettings settings;
    private final MinecraftVersion version;
    private final Predicate<String> jarEntryFilter;

    /**
     * Creates a new execution
     *
     * @param settings       the settings
     * @param version        the version metadata
     * @param jarEntryFilter a filter which decides which jar entries are extracted
     */
    public VineyardDecompileExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final MinecraftVersion version, Predicate<String> jarEntryFilter) {
        this.settings = settings;
        this.version = version;
        this.jarEntryFilter = jarEntryFilter;
    }

    /**
     * Creates a new execution
     *
     * @param settings the settings
     * @param version  the version
     */
    public VineyardDecompileExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final MinecraftVersion version) {
        this(settings, version, (s) -> s.contains("com/mojang") || s.contains("net/minecraft"));
    }

    @Override
    public Result<Boolean, IllegalStateException> run() {
        final Path remappedServer = settings.workingDirectory().resolve(VineyardConstants.PATH_REMAPPED_SERVER_DESTINATION);
        try {
            final Path decompile = settings.workingDirectory().resolve(VineyardConstants.PATH_DECOMPILE_DESTINATION.formatted(version.data().downloadEntries().get(VersionData.SERVER).sha1()));
            if (Files.notExists(decompile.getParent())) {
                settings.whenDebug(VineyardConstants.LOG_CREATE_DECOMPILE_DIRECTORIES);
                Files.createDirectories(decompile.getParent());
            }

            final Path decompileClasses = decompile.resolve("classes");
            if (Files.notExists(decompileClasses)) {
                settings.whenDebug(VineyardConstants.LOG_CREATE_DECOMPILE_CLASSES_DIRECTORIES);
                Files.createDirectories(decompileClasses);
                VineyardUtils.unzip(remappedServer, decompileClasses, this.jarEntryFilter);
            }

            final Path decompileJava = decompile.resolve("java");
            if (Files.notExists(decompileJava)) {
                final Path vineflower = settings.workingDirectory().resolve(VineyardConstants.PATH_VINEFLOWER_DESTINATION);
                settings.whenDebug(VineyardConstants.LOG_CREATE_DECOMPILE_JAVA_DIRECTORIES);
                Files.createDirectories(decompileJava);
                new ProcessBuilder()
                        .command("java", "-jar",
                                vineflower.toString(), "-udv=1", "-ump=0", "-asc=1", "-rbr=0", decompileClasses.toString(), decompileJava.toString())
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                settings.whenDebug(VineyardConstants.LOG_CURRENT_VERSION_ALREADY_DECOMPILED.formatted(settings.minecraftVersion(), decompile));
            }
        } catch (IOException | InterruptedException e) {
            return failure(new IllegalStateException(e));
        }

        return success(true);
    }
}
