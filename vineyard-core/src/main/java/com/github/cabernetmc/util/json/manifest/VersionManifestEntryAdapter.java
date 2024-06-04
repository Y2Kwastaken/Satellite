package com.github.cabernetmc.util.json.manifest;

import com.github.cabernetmc.meta.manifest.VersionManifestEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.URI;

public class VersionManifestEntryAdapter implements JsonDeserializer<VersionManifestEntry> {

    @Override
    public VersionManifestEntry deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final String id = parent.get("id").getAsString();
        final String releaseType = parent.get("type").getAsString();
        final URI url = URI.create(parent.get("url").getAsString());
        final String sha1 = parent.get("sha1").getAsString();
        return new VersionManifestEntry(id, releaseType, url, sha1);
    }
}
