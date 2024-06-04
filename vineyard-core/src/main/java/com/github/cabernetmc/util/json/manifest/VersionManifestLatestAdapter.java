package com.github.cabernetmc.util.json.manifest;

import com.github.cabernetmc.meta.manifest.VersionManifestLatest;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class VersionManifestLatestAdapter implements JsonDeserializer<VersionManifestLatest> {

    @Override
    public VersionManifestLatest deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        return new VersionManifestLatest(parent.get("release").getAsString(), parent.get("snapshot").getAsString());
    }
}
