package io.github.cabernetmc.json.manifest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.github.cabernetmc.meta.manifest.VersionManifest;
import io.github.cabernetmc.meta.manifest.VersionManifestEntry;
import io.github.cabernetmc.meta.manifest.VersionManifestLatest;

import java.lang.reflect.Type;
import java.util.List;

public class VersionManifestAdapter implements JsonDeserializer<VersionManifest> {

    @Override
    public VersionManifest deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final VersionManifestLatest latest = context.deserialize(parent.get("latest"), VersionManifestLatest.class);
        final List<VersionManifestEntry> entries = context.deserialize(parent.get("versions"), new TypeToken<List<VersionManifestEntry>>() {}.getType());
        return new VersionManifest(latest, entries);
    }

}
