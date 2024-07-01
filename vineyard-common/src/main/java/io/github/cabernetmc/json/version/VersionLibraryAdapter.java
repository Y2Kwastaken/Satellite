package io.github.cabernetmc.json.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.cabernetmc.meta.version.VersionLibrary;
import io.github.cabernetmc.meta.version.VersionLibraryArtifact;

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
