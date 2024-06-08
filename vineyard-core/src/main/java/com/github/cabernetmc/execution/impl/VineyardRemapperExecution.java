package com.github.cabernetmc.execution.impl;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import com.github.cabernetmc.execution.VineyardExecutionSettings;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
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
import java.util.Set;

public class VineyardRemapperExecution extends AbstractVineyardExecution {

    private static final String TINY_MAPS = "mappings/mappings.tiny";
    private static final String REMAP_OUTPUT = "server/server.jar";

    private Path tinyMaps;
    private Path remapOutput;

    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public VineyardRemapperExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        super(settings, requestHelper);
        this.dependencyClasses.addAll(Set.of(VineyardDownloadExecution.class, VineyardExtractExecution.class));
    }

    @Override
    public void execute() {
        final var downloadExecution = (VineyardDownloadExecution) dependencies.get(VineyardDownloadExecution.class);
        final var extractExecution = (VineyardExtractExecution) dependencies.get(VineyardExtractExecution.class);

        convertMappings(settings.workingDirectory(), downloadExecution.serverMappings());
        remapServer(settings.workingDirectory(), extractExecution.server());
    }

    @Override
    public void verifyExecution() {
        assert this.tinyMaps != null && this.remapOutput != null;
        assert Files.exists(tinyMaps) && Files.exists(this.remapOutput);
    }

    @NotNull
    public Path tinyMaps() {
        return this.tinyMaps;
    }

    @NotNull
    public Path server() {
        return this.remapOutput;
    }

    private void convertMappings(final Path work, final Path severMappings) {
        final Path tinyMaps = work.resolve(TINY_MAPS);
        this.tinyMaps = tinyMaps;

        if (!settings.ignoreCaches() && Files.exists(tinyMaps)) {
            if (settings.debug()) {
                System.out.println("tiny mappings file exists so it will not be regenerated");
            }
            return;
        }

        final VisitableMappingTree tree = new MemoryMappingTree();
        try {
            MappingReader.read(severMappings, MappingFormat.PROGUARD_FILE, tree);
            tree.accept(MappingWriter.create(tinyMaps, MappingFormat.TINY_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void remapServer(final Path work, Path server) {
        final Path outputServerJar = work.resolve(REMAP_OUTPUT);
        this.remapOutput = outputServerJar;

        if (!settings.ignoreCaches() && Files.exists(outputServerJar)) {
            if (settings.debug()) {
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
            consumerPath.addNonClassFiles(server);
            remapper.readInputs(server);
            remapper.apply(consumerPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            remapper.finish();
        }
    }

}
