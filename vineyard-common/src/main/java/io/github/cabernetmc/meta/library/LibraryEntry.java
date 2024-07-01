package io.github.cabernetmc.meta.library;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a library entry within a list of libraries. Supposedly found within the libraries.txt file
 *
 * @param sha1       the expected sha1 of the dependency
 * @param dependency the expected dependency string
 * @param classPath  the classpath of the dependency
 */
public record LibraryEntry(@NotNull String sha1, @NotNull String dependency, @NotNull String classPath) {

    /**
     * Creates a library entry from a string, given the string meets the needed requirements
     *
     * @param entry the entry string
     * @return the LibraryEntry object
     */
    @NotNull
    public static LibraryEntry fromString(@NotNull final String entry) {
        final var split = entry.trim().split("\t");
        return new LibraryEntry(split[0], split[1], split[2]);
    }
}
