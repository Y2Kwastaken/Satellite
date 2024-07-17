package io.github.orbitemc;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipInputStream;

/**
 * Provides useful general utilities
 */
public final class SatelliteUtils {

    private SatelliteUtils() {
        throw new UnsupportedOperationException("Can not initialize utility class %s".formatted(getClass()));
    }

    /**
     * Unzips a given zip file
     *
     * @param zipFile    the zip file to unzip
     * @param target     the target
     * @param fileFilter any filtration required
     * @throws IllegalStateException if an IOException occurs
     */
    public static void unzip(@NotNull final Path zipFile, @NotNull final Path target, @NotNull Predicate<String> fileFilter, @NotNull Consumer<String> log) throws IllegalArgumentException {
        try {
            Files.createDirectories(target);
            try (var zip = new ZipInputStream(Files.newInputStream(zipFile))) {
                var entry = zip.getNextEntry();

                while (entry != null) {
                    if (!fileFilter.test(entry.getName())) {
                        entry = zip.getNextEntry();
                        log.accept("Filtered log entry %s".formatted(entry.getName()));
                        continue;
                    }

                    Path output = target.resolve(entry.getName());

                    if (entry.isDirectory()) {
                        Files.createDirectories(output);
                        log.accept("Created directory %s".formatted(output));
                        entry = zip.getNextEntry();
                        continue;
                    } else if (Files.notExists(output.getParent())) {
                        Files.createDirectories(output);
                        log.accept("Created directory %s".formatted(output));
                    }


                    Files.copy(zip, output, StandardCopyOption.REPLACE_EXISTING);
                    log.accept("Successfully copied %s to %s".formatted(entry.getName(), output));
                    entry = zip.getNextEntry();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Digs within a jar file and extracts a given element to a file outside of the jar
     *
     * @param jarFile     the jarFile to extract something from
     * @param destination the destination to put that extraction in
     * @throws IllegalStateException if an IOException occurs
     */
    public static void jarExtract(@NotNull final Path jarFile, @NotNull final Path destination, @NotNull final String internalPath) throws IllegalStateException {
        try (final var jar = new JarFile(jarFile.toFile())) {
            try (final var reader = jar.getInputStream(jar.getJarEntry(internalPath))) {
                if (Files.notExists(destination.getParent())) {
                    Files.createDirectories(destination.getParent());
                }

                if (Files.notExists(destination)) {
                    Files.createFile(destination);
                }

                try (final var output = Files.newOutputStream(destination)) {
                    output.write(reader.readAllBytes());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
