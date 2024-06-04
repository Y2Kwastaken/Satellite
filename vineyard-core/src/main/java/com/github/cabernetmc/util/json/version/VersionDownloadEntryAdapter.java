package com.github.cabernetmc.util.json.version;

import com.github.cabernetmc.meta.version.VersionDownloadEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.URI;

public class VersionDownloadEntryAdapter implements JsonDeserializer<VersionDownloadEntry> {
    @Override
    public VersionDownloadEntry deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final var sha1 = parent.get("sha1").getAsString();
        final var size = parent.get("size").getAsInt();
        final var url = URI.create(parent.get("url").getAsString());
        return new VersionDownloadEntry(sha1, size, url);
    }
}
