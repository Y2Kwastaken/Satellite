package com.github.cabernetmc.util;

import com.github.cabernetmc.meta.manifest.VersionManifest;
import com.github.cabernetmc.meta.manifest.VersionManifestEntry;
import com.github.cabernetmc.meta.manifest.VersionManifestLatest;
import com.github.cabernetmc.meta.version.VersionData;
import com.github.cabernetmc.meta.version.VersionDownloadEntry;
import com.github.cabernetmc.meta.version.VersionLibrary;
import com.github.cabernetmc.meta.version.VersionLibraryArtifact;
import com.github.cabernetmc.util.json.manifest.VersionManifestAdapter;
import com.github.cabernetmc.util.json.manifest.VersionManifestEntryAdapter;
import com.github.cabernetmc.util.json.manifest.VersionManifestLatestAdapter;
import com.github.cabernetmc.util.json.version.VersionDataAdapter;
import com.github.cabernetmc.util.json.version.VersionDownloadEntryAdapter;
import com.github.cabernetmc.util.json.version.VersionLibraryAdapter;
import com.github.cabernetmc.util.json.version.VersionLibraryArtifactAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipInputStream;

/**
 * Public Utilities for Vineyard
 */
public final class VineyardUtils {
    public static final URI PISTON_META_LINK = URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
    public static final URI VINEFLOWER_LINK = URI.create("https://github.com/Vineflower/vineflower/releases/download/1.10.1/vineflower-1.10.1.jar");
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
     * Digs within a jar file and extracts a given element to a file outside of the jar
     *
     * @param jarFile     the jarFile to extract something from
     * @param destination the destination to put that extraction in
     */
    public static void jarExtract(@NotNull final Path jarFile, @NotNull final Path destination, @NotNull final String internalPath) {
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Digs within a jar file for certain files
     *
     * @param jarFile      the jar file to dig in
     * @param internalPath the internal path for the file to be dug up
     * @return the text within that file
     */
    @NotNull
    public static List<String> jarDig(@NotNull final Path jarFile, @NotNull final String internalPath) {
        try (final var jar = new JarFile(jarFile.toFile())) {
            try (final var reader = new BufferedReader(new InputStreamReader(jar.getInputStream(jar.getJarEntry(internalPath))))) {
                return reader.lines().toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Downloads a file from the given url
     *
     * @param url         the url to download from
     * @param destination the destination the file is downloaded to
     * @param replace     whether to replace an existing file if it exists already
     */
    public static void download(@NotNull final URI url, @NotNull final Path destination, final boolean replace) {
        try {
            final URL website = url.toURL();
            if (Files.notExists(destination.getParent())) {
                Files.createDirectories(destination.getParent());
            }
            Files.copy(website.openStream(), destination, replace ? StandardCopyOption.REPLACE_EXISTING : null);
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    /**
     * Creates a JsonObject from a HTTP request.
     * <p>
     * Note this method automatically assumes, you as a caller have permission to send a http GET request. The request
     * location responds with a JsonObject in specific. As well as the assumption that this should be used with piston
     * meta data api
     *
     * @param client the http client
     * @param url    the target url
     * @return a json object
     */
    @NotNull
    public static JsonObject fromHttpRequest(@NotNull final HttpClient client, @NotNull final URI url) {
        final HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        final JsonObject parent;
        try {
            return GSON.fromJson(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), JsonObject.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
