package io.github.orbitemc.patcher;

import io.codechicken.diffpatch.util.IOValidationException;
import io.codechicken.diffpatch.util.Input;
import io.codechicken.diffpatch.util.Output;
import net.covers1624.quack.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SafeFolderMultiOutput extends Output.MultiOutput {

    private final Path outputFolder;
    private final Path inputFolder;

    public SafeFolderMultiOutput(final Path outputFolder, final Path inputFolder) {
        this.outputFolder = outputFolder;
        this.inputFolder = inputFolder;
    }

    @Override
    public void validate(@NotNull final String kind) throws IOValidationException {
        if (Files.exists(outputFolder) && !Files.isDirectory(outputFolder)) {
            throw new IOValidationException("Output '" + kind + "' already exists and is not a file.");
        }
    }

    @Override
    public void open(final boolean clearOutput) throws IOException {
        final Set<Path> inputHeads;
        try (final Stream<Path> list = Files.list(inputFolder)) {
            inputHeads = list.map(Path::getFileName).collect(Collectors.toSet());
        }
        Files.walkFileTree(outputFolder, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                if (dir.getParent().equals(outputFolder)) {
                    if (inputHeads.contains(dir.getFileName())) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.SKIP_SIBLINGS;
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                if (Files.deleteIfExists(file)) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                try (final Stream<Path> stream = Files.list(dir)) {
                    if (stream.findFirst().isPresent()) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        Files.delete(dir);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void write(@NotNull final String path, @NotNull final byte[] data) throws IOException {
        Files.write(IOUtils.makeParents(outputFolder.resolve(path)), data);
    }

    @Override
    public void close() throws IOException {
    }


    @Override
    public boolean isSamePath(@NotNull Input input) {
        if (!(input instanceof Input.FolderMultiInput)) return false;

        return outputFolder.equals(((Input.FolderMultiInput) input).folder);
    }
}
