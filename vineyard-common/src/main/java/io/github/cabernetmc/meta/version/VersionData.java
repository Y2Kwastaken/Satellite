package io.github.cabernetmc.meta.version;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents all data needed for a version
 *
 * @param downloadEntries the download entries
 * @param libraries       all libraries for the version
 */
public record VersionData(@NotNull Map<String, VersionDownloadEntry> downloadEntries,
                          @NotNull List<VersionLibrary> libraries) {

    public static final String SERVER = "server";
    public static final String SERVER_MAPPINGS = "server_mappings";
    public static final String CLIENT = "client";
    public static final String CLIENT_MAPPINGS = "client_mappings";

}
