package io.github.kryptidemc;

import java.net.URI;

public final class SpecterConstants {
    public static final URI VINEFLOWER_LINK = URI.create("https://github.com/Vineflower/vineflower/releases/download/1.10.1/vineflower-1.10.1.jar");

    private SpecterConstants() {
        throw new UnsupportedOperationException("Can not initialize utility class %s".formatted(getClass()));
    }
}
