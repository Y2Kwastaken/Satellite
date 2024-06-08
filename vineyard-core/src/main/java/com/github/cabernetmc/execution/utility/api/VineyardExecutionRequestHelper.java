package com.github.cabernetmc.execution.utility.api;

import com.github.cabernetmc.util.function.ThrowingConsumer;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;

/**
 * Manages different methods that can make web or net requests
 */
public interface VineyardExecutionRequestHelper {
    /**
     * Downloads a file from the given url to the specified destination
     * <p>
     * If the destination already has a file the download will be cancelled
     *
     * @param downloadLink the download link
     * @param destination  the destination of the download
     * @return true if the download is successful
     */
    boolean download(@NotNull final URI downloadLink, @NotNull final Path destination);

    /**
     * Downloads a file from the given url to the specified destination
     * <p>
     * If the destination already has a file and replace is false the download will be cancelled
     *
     * @param downloadLink the download link
     * @param destination  the destination of the download
     * @param replace      whether or not to replace a file that exists at the destination if one exists
     * @return true if the download is successful
     */
    boolean download(@NotNull final URI downloadLink, @NotNull final Path destination, final boolean replace);

    /**
     * Uses a HTTPClient negating swallowing all checked exceptions
     *
     * @param client the client
     * @param use    the use for the client
     */
    default void use(@NotNull final HttpClient client, ThrowingConsumer<HttpClient> use) {
        try {
            use.use(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
