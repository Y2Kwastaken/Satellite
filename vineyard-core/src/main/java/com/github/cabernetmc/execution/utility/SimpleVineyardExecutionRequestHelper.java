package com.github.cabernetmc.execution.utility;

import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SimpleVineyardExecutionRequestHelper implements VineyardExecutionRequestHelper {

    @Override
    public boolean download(@NotNull final URI downloadLink, @NotNull final Path destination) {
        return download(downloadLink, destination, false);
    }

    @Override
    public boolean download(@NotNull final URI downloadLink, @NotNull final Path destination, final boolean replace) {
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


}
