package com.github.cabernetmc.step;

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

public class VineyardRemapStep implements VineyardStep {

    public static final String TINY_MAPS = "mappings/mappings.tiny";
    public static final String REMAP_OUTPUT = "server/server.jar";

    private boolean debug;
    private final Path work;
    private final boolean ignoreCaches;

    public VineyardRemapStep(final boolean debug, @NotNull final Path work, final boolean ignoreCaches) {
        this.debug = debug;
        this.work = work;
        this.ignoreCaches = ignoreCaches;
    }

    @Override
    public void run() {
        convertMappings();
        remapServer();
    }

    @Override
    public void verifyCompleted() {
        assert Files.exists(work.resolve(TINY_MAPS));
        assert Files.exists(work.resolve(REMAP_OUTPUT));
    }

    private void convertMappings() {
        final Path tinyMaps = work.resolve(TINY_MAPS);

        if (!ignoreCaches && Files.exists(tinyMaps)) {
            if (debug) {
                System.out.println("tiny mappings file exists so it will not be regenerated");
            }
            return;
        }

        final VisitableMappingTree tree = new MemoryMappingTree();
        try {
            MappingReader.read(work.resolve(VineyardDownloadStep.SERVER_MAPPINGS), MappingFormat.PROGUARD_FILE, tree);
            tree.accept(MappingWriter.create(tinyMaps, MappingFormat.TINY_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void remapServer() {
        final Path outputServerJar = work.resolve(REMAP_OUTPUT);

        if (!ignoreCaches && Files.exists(outputServerJar)) {
            if (debug) {
                System.out.println("Remapped server jar exists so it will not be remapped again");
            }
            return;
        }

        final var remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(work.resolve(TINY_MAPS), "target", "source"))
                .resolveMissing(true)
                .rebuildSourceFilenames(true)
                .renameInvalidLocals(true)
                .build();

        try (final OutputConsumerPath consumerPath = new OutputConsumerPath.Builder(outputServerJar).build()) {
            consumerPath.addNonClassFiles(work.resolve(VineyardExtractStep.SERVER_EXTRACTED));
            remapper.readInputs(work.resolve(VineyardExtractStep.SERVER_EXTRACTED));
            remapper.apply(consumerPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            remapper.finish();
        }
    }
}
