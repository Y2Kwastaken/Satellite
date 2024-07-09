package io.github.orbitemc;

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
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Provides useful functions regarding remapping
 */
public final class SpecterRemapper {

    /*
     * Thanks fabric team
     * https://github.com/FabricMC/fabric-loom/blob/c4d36fac4ea7ccd1ef9526aa138139a154c8f581/src/main/java/net/fabricmc/loom/util/TinyRemapperHelper.java#L77
     */
    private static final Map<String, String> JSR_TO_JETBRAINS = Map.of(
            "javax/annotation/Nullable", "org/jetbrains/annotations/Nullable",
            "javax/annotation/Nonnull", "org/jetbrains/annotations/NotNull",
            "javax/annotation/concurrent/Immutable", "org/jetbrains/annotations/Unmodifiable");
    private static final Pattern MC_LV_PATTERN = Pattern.compile("\\$\\$\\d+");

    /**
     * Converts the mappings at the provided mappings path to fabric's tinyMappings
     *
     * @param mappings     the mappings
     * @param format       the format of the original mappings
     * @param tinyMappings tinyMappings output location
     * @throws IllegalStateException thrown if an IOException occurs
     */
    public static void convertMappings(@NotNull final Path mappings, @NotNull final MappingFormat format, @NotNull final Path tinyMappings) throws IllegalStateException {
        final VisitableMappingTree mappingTree = new MemoryMappingTree();
        try {
            MappingReader.read(mappings, format, mappingTree);
            mappingTree.accept(MappingWriter.create(tinyMappings, MappingFormat.TINY_FILE));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Remaps the targeted jar file to the using mappings of the tiny format into the remapped jar
     *
     * @param jarFile       the jar file
     * @param outputJarFile the output, remapped, jar file
     * @param tinyMappings  the tiny mappings
     * @param reverse       applies mappings in reverse, target mapping into source remapping.
     * @throws IllegalStateException thrown if an IOException occurs
     */
    public static void remapJarFile(@NotNull final Path jarFile, @NotNull final Path outputJarFile, @NotNull Path tinyMappings, boolean reverse) throws IllegalStateException {
        final String[] mappingOrder = new String[2];
        if (reverse) {
            mappingOrder[0] = "source";
            mappingOrder[1] = "target";
        } else {
            mappingOrder[0] = "target";
            mappingOrder[1] = "source";
        }

        final TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(tinyMappings, mappingOrder[0], mappingOrder[1]))
                .withMappings(out -> JSR_TO_JETBRAINS.forEach(out::acceptClass))
                .renameInvalidLocals(true)
                .invalidLvNamePattern(MC_LV_PATTERN)
                .inferNameFromSameLvIndex(true)
                .build();

        try (final OutputConsumerPath consumerPath = new OutputConsumerPath.Builder(outputJarFile).build()) {
            consumerPath.addNonClassFiles(jarFile);
            remapper.readInputs(jarFile);
            remapper.apply(consumerPath);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            remapper.finish();
        }
    }

}
