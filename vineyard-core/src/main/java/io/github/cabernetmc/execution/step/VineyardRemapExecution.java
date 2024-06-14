package io.github.cabernetmc.execution.step;

import io.github.cabernetmc.execution.ExecutionError;
import io.github.cabernetmc.execution.VineyardExecutionSettings;
import io.github.cabernetmc.util.Result;
import io.github.cabernetmc.util.Result.Failure;
import io.github.cabernetmc.util.Results;
import io.github.cabernetmc.util.VineyardConstants;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.MappingWriter;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.mappingio.tree.VisitableMappingTree;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import static io.github.cabernetmc.util.Result.failure;
import static io.github.cabernetmc.util.Result.success;

/**
 * Remaps the extracted jar from {@link VineyardExtractExecution} or any source given its put in the correct directory
 */
public class VineyardRemapExecution implements Results<Boolean, IllegalStateException> {

    /*
     * Thanks fabric team
     * https://github.com/FabricMC/fabric-loom/blob/c4d36fac4ea7ccd1ef9526aa138139a154c8f581/src/main/java/net/fabricmc/loom/util/TinyRemapperHelper.java#L77
     */
    private static final Map<String, String> JSR_TO_JETBRAINS = Map.of(
            "javax/annotation/Nullable", "org/jetbrains/annotations/Nullable",
            "javax/annotation/Nonnull", "org/jetbrains/annotations/NotNull",
            "javax/annotation/concurrent/Immutable", "org/jetbrains/annotations/Unmodifiable");
    private static final Pattern MC_LV_PATTERN = Pattern.compile("\\$\\$\\d+");

    private final VineyardExecutionSettings settings;

    /**
     * Creates a new execution
     *
     * @param settings the settings
     */
    public VineyardRemapExecution(@NotNull final VineyardExecutionSettings settings) {
        this.settings = settings;
    }

    @Override
    public Result<Boolean, IllegalStateException> run() {
        switch (convertMappings()) {
            case Result.Success<?, ?> ignored -> {
            }
            case Failure<?, IllegalStateException> failure -> {
                throw new ExecutionError(failure.exception);
            }
            default -> throw new ExecutionError(VineyardConstants.LOG_RESULT_DEFAULTED);
        }

        switch (remapServer()) {
            case Result.Success<?, ?> ignored -> {
            }
            case Failure<?, IllegalStateException> failure -> {
                throw new ExecutionError(failure.exception);
            }
            default -> throw new ExecutionError(VineyardConstants.LOG_RESULT_DEFAULTED);

        }
        return success(true);
    }

    private Result<Boolean, IllegalStateException> convertMappings() {
        final Path tinyMaps = settings.workingDirectory().resolve(VineyardConstants.PATH_TINY_MAPS_DESTINATION);
        final Path serverMappings = settings.workingDirectory().resolve(VineyardConstants.PATH_MAPPINGS_DESTINATION);
        if (!settings.ignoreCaches() && Files.exists(tinyMaps)) {
            settings.whenDebug(VineyardConstants.LOG_TINY_MAPS_EXIST);
            return success(true);
        }

        settings.whenDebug(VineyardConstants.LOG_TINY_MAPS_CREATING);
        final VisitableMappingTree tree = new MemoryMappingTree();
        try {
            MappingReader.read(serverMappings, MappingFormat.PROGUARD_FILE, tree);
            tree.accept(MappingWriter.create(tinyMaps, MappingFormat.TINY_FILE));
        } catch (IOException e) {
            return failure(new IllegalStateException(e));
        }

        return success(true);
    }

    private Result<Boolean, IllegalStateException> remapServer() {
        final Path tinyMaps = settings.workingDirectory().resolve(VineyardConstants.PATH_TINY_MAPS_DESTINATION);
        final Path remappedServer = settings.workingDirectory().resolve(VineyardConstants.PATH_REMAPPED_SERVER_DESTINATION);
        final Path obfuscatedServer = settings.workingDirectory().resolve(VineyardConstants.PATH_OBFUSCATED_SERVER_DESTINATION);

        if (!settings.ignoreCaches() && Files.exists(remappedServer)) {
            settings.whenDebug(VineyardConstants.LOG_REMAP_SERVER_EXIST);
            return success(true);
        }

        settings.whenDebug(VineyardConstants.LOG_REMAP_SERVER_CREATING);
        final var remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(tinyMaps, "target", "source"))
                .withMappings(out -> JSR_TO_JETBRAINS.forEach(out::acceptClass))
                .renameInvalidLocals(true)
                .invalidLvNamePattern(MC_LV_PATTERN)
                .inferNameFromSameLvIndex(true)
                .build();
        try (final OutputConsumerPath consumerPath = new OutputConsumerPath.Builder(remappedServer).build()) {
            consumerPath.addNonClassFiles(obfuscatedServer);
            remapper.readInputs(obfuscatedServer);
            remapper.apply(consumerPath);
        } catch (IOException e) {
            return failure(new IllegalStateException(e));
        } finally {
            remapper.finish();
        }

        return success(true);
    }
}
