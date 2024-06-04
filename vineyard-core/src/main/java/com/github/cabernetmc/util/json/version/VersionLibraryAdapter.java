package com.github.cabernetmc.util.json.version;

import com.github.cabernetmc.meta.version.VersionLibrary;
import com.github.cabernetmc.meta.version.VersionLibraryArtifact;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class VersionLibraryAdapter implements JsonDeserializer<VersionLibrary> {

    @Override
    public VersionLibrary deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var object = element.getAsJsonObject();
        final VersionLibraryArtifact artifact = context.deserialize(object.getAsJsonObject("downloads").getAsJsonObject("artifact"), VersionLibraryArtifact.class);
        final String name = object.get("name").getAsString();
        return new VersionLibrary(name, artifact);
    }
}
