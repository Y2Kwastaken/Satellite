package io.github.cabernetmc.json.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.cabernetmc.meta.version.VersionData;
import io.github.cabernetmc.meta.version.VersionDownloadEntry;
import io.github.cabernetmc.meta.version.VersionLibrary;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionDataAdapter implements JsonDeserializer<VersionData> {
    @Override
    public VersionData deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();

        final Map<String, VersionDownloadEntry> downloadEntries = new HashMap<>();
        final var downloads = parent.getAsJsonObject("downloads");
        for (final String downloadKey : downloads.keySet()) {
            downloadEntries.put(downloadKey, context.deserialize(downloads.get(downloadKey), VersionDownloadEntry.class));
        }

        final List<VersionLibrary> libraries = new ArrayList<>();
        for (final JsonElement library : parent.getAsJsonArray("libraries")) {
            libraries.add(context.deserialize(library, VersionLibrary.class));
        }

        return new VersionData(downloadEntries, libraries);
    }
}
