package io.github.cabernetmc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cabernetmc.json.manifest.VersionManifestAdapter;
import io.github.cabernetmc.json.manifest.VersionManifestEntryAdapter;
import io.github.cabernetmc.json.manifest.VersionManifestLatestAdapter;
import io.github.cabernetmc.json.version.VersionDataAdapter;
import io.github.cabernetmc.json.version.VersionDownloadEntryAdapter;
import io.github.cabernetmc.json.version.VersionLibraryAdapter;
import io.github.cabernetmc.json.version.VersionLibraryArtifactAdapter;
import io.github.cabernetmc.meta.manifest.VersionManifest;
import io.github.cabernetmc.meta.manifest.VersionManifestEntry;
import io.github.cabernetmc.meta.manifest.VersionManifestLatest;
import io.github.cabernetmc.meta.version.VersionData;
import io.github.cabernetmc.meta.version.VersionDownloadEntry;
import io.github.cabernetmc.meta.version.VersionLibrary;
import io.github.cabernetmc.meta.version.VersionLibraryArtifact;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipInputStream;

import static io.github.cabernetmc.util.Result.failure;
import static io.github.cabernetmc.util.Result.success;

/**
 * Public Utilities for Vineyard
 */
public class VineyardUtils {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(VersionManifestEntry.class, new VersionManifestEntryAdapter())
            .registerTypeAdapter(VersionManifestLatest.class, new VersionManifestLatestAdapter())
            .registerTypeAdapter(VersionManifest.class, new VersionManifestAdapter())
            .registerTypeAdapter(VersionLibraryArtifact.class, new VersionLibraryArtifactAdapter())
            .registerTypeAdapter(VersionLibrary.class, new VersionLibraryAdapter())
            .registerTypeAdapter(VersionDownloadEntry.class, new VersionDownloadEntryAdapter())
            .registerTypeAdapter(VersionData.class, new VersionDataAdapter())
            .create();

    /**
     * Downloads a file from the provided URI to the provided destination, by default if a file is at the destination an
     * error occurs
     *
     * @param downloadLink the download link
     * @param destination  the destination of the file
     * @return true if the download succeeded, otherwise false
     */
    public static boolean download(@NotNull final URI downloadLink, @NotNull final Path destination) {
        return download(downloadLink, destination, false);
    }

    /**
     * Downloads a file from provided URI to the provided destination
     *
     * @param downloadLink the download link
     * @param destination  the destination of the file
     * @param replace      whether or not to replace a file given there is one at the location
     * @return true if the download succeeded, otherwise false
     */
    public static boolean download(@NotNull final URI downloadLink, @NotNull final Path destination, final boolean replace) {
        try {
            final URL website = downloadLink.toURL();
            if (Files.notExists(destination.getParent())) {
                Files.createDirectories(destination.getParent());
            }
            Files.copy(website.openStream(), destination, replace ? StandardCopyOption.REPLACE_EXISTING : null);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Digs within a jar file and extracts a given element to a file outside of the jar
     *
     * @param jarFile     the jarFile to extract something from
     * @param destination the destination to put that extraction in
     */
    public static Result<Boolean, IOException> jarExtract(@NotNull final Path jarFile, @NotNull final Path destination, @NotNull final String internalPath) {
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
            return success(true);
        } catch (IOException e) {
            return failure(e);
        }
    }

    /**
     * Unzips a given zip file
     *
     * @param zipFile    the zip file to unzip
     * @param target     the target
     * @param fileFilter any filtration required
     */
    public static void unzip(@NotNull final Path zipFile, @NotNull final Path target, Predicate<String> fileFilter) {
        try {
            Files.createDirectories(target);
            try (var zip = new ZipInputStream(Files.newInputStream(zipFile))) {
                var entry = zip.getNextEntry();

                while (entry != null) {
                    if (!fileFilter.test(entry.getName())) {
                        entry = zip.getNextEntry();
                        continue;
                    }

                    Path output = target.resolve(entry.getName());

                    if (entry.isDirectory()) {
                        Files.createDirectories(output);
                        entry = zip.getNextEntry();
                        continue;
                    } else if (Files.notExists(output.getParent())) {
                        Files.createDirectories(output);
                    }


                    Files.copy(zip, output, StandardCopyOption.REPLACE_EXISTING);
                    entry = zip.getNextEntry();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
