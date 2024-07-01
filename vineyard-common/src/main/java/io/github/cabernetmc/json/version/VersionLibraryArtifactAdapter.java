package io.github.cabernetmc.json.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.cabernetmc.meta.version.VersionLibraryArtifact;

import java.lang.reflect.Type;
import java.net.URI;

public class VersionLibraryArtifactAdapter implements JsonDeserializer<VersionLibraryArtifact> {

    @Override
    public VersionLibraryArtifact deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final var path = parent.get("path").getAsString();
        final var sha1 = parent.get("sha1").getAsString();
        final var size = parent.get("size").getAsInt();
        final var url = URI.create(parent.get("url").getAsString());
        return new VersionLibraryArtifact(path, sha1, size, url);
    }
}
