package io.github.cabernetmc.util;

import java.net.URI;

/**
 * Constants that are used within various VineyardExecutions
 */
public final class VineyardConstants {
    public static final String LOG_RESULT_DEFAULTED = "Result was neither success or failure! This should be reported immediately, this error should never occur!";
    public static final String LOG_RESULT_META_SUCCESS = "Found expected metadata for version %s.";
    public static final String LOG_RESULT_META_FAILURE = "Could not find expected metadata for version %s. Does this version exist?";

    public static final String LOG_DOWNLOAD_BOOTSTRAP = "Downloading Minecraft server bootstrap for version %s from %s with sha1 of %s";
    public static final String LOG_DOWNLOAD_BOOTSTRAP_FAILURE = "Failed to download bootstrap for unknown reason";
    public static final String LOG_DOWNLOAD_MAPPINGS = "Downloading Minecraft server mappings for version %s from %s with sha1 of %s";
    public static final String LOG_DOWNLOAD_MAPPINGS_FAILURE = "Failed to download mappings for unknown reason";
    public static final String LOG_DOWNLOAD_VINEFLOWER = "Downloading vineflower from %s";
    public static final String LOG_DOWNLOAD_VINEFLOWER_FAILURE = "Failed to download vineflower for an unknown reason";

    public static final String LOG_EXTRACT_OBFUSCATED_SERVER = "Extracting the obfuscated minecraft server to %s";
    public static final String LOG_EXTRACT_LIBRARIES_LIST = "Extracting the libraries list to %s";

    public static final String LOG_TINY_MAPS_EXIST = "Not creating new tiny maps because they already exist";
    public static final String LOG_TINY_MAPS_CREATING = "Converting mojang mappings to tiny maps";
    public static final String LOG_REMAP_SERVER_EXIST = "Not remapping obfuscated server because one already exists";
    public static final String LOG_REMAP_SERVER_CREATING = "Remapping obfuscated server from obfuscated to mojang mappings";

    public static final String LOG_CREATE_DECOMPILE_DIRECTORIES = "Created directories needed for decompilation";
    public static final String LOG_CREATE_DECOMPILE_CLASSES_DIRECTORIES = "Created directories for class extraction";
    public static final String LOG_CREATE_DECOMPILE_JAVA_DIRECTORIES = "Created directories for jar decompilation";
    public static final String LOG_CURRENT_VERSION_ALREADY_DECOMPILED = "The version %s is already decompiled at the folder %s.";

    public static final String PATH_BOOTSTRAP_DESTINATION = "server/server-bootstrap.jar";
    public static final String PATH_MAPPINGS_DESTINATION = "mappings/server-mappings.txt";
    public static final String PATH_VINEFLOWER_DESTINATION = "vineflower.jar";
    public static final String PATH_OBFUSCATED_SERVER_DESTINATION = "server/server-obfuscated.jar";
    public static final String PATH_LIBRARIES_LIST_DESTINATION = "server/server-libraries.list";
    public static final String PATH_TINY_MAPS_DESTINATION = "mappings/mappings.tiny";
    public static final String PATH_REMAPPED_SERVER_DESTINATION = "server/server.jar";
    public static final String PATH_DECOMPILE_DESTINATION = "decompile-%s";


    public static final URI PISTON_META_LINK = URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
    public static final URI VINEFLOWER_LINK = URI.create("https://github.com/Vineflower/vineflower/releases/download/1.10.1/vineflower-1.10.1.jar");

    private VineyardConstants() {
        throw new UnsupportedOperationException("Can not create instance of constants class");
    }
}
