package com.github.cabernetmc.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Option that can contain or not contain a value
 *
 * @param <E> the entry type
 * @since 1.0.0-SNAPSHOT
 */
public sealed class Option<E> permits Option.None, Option.Some {

    /**
     * Commits an operation given that an option is present
     *
     * @param consumer the operation on present
     * @since 1.0.0-SNAPSHOT
     */
    public void ifPresent(@NotNull final Consumer<E> consumer) {
        if (this instanceof Option.Some<E> some) {
            consumer.accept(some.some);
        }
    }

    /**
     * Maps the given option to another value if available. Otherwise None is returned
     *
     * @param mapper the mapper
     * @param <R>    the returned type
     * @return the new option
     * @since 1.0.0-SNAPSHOT
     */
    public <R> Option<R> map(@NotNull final Function<E, R> mapper) {
        if (!(this instanceof Some<E> some)) {
            return new None<>();
        }

        return new Some<>(mapper.apply(some.some));
    }

    /**
     * Gets some value or throws
     *
     * @return the value
     * @throws IllegalStateException if no value was found
     * @since 1.0.0-SNAPSHOT
     */
    public E orThrow() throws IllegalStateException {
        if (!(this instanceof Some<E> some)) {
            throw new IllegalStateException("Unable to find some value");
        }

        return some.some;
    }

    /**
     * Gets some value or the other value
     *
     * @param value the non null value to use instead
     * @return the value
     * @since 1.0.0-SNAPSHOT
     */
    @NotNull
    public E orElse(@NotNull final E value) {
        if (!(this instanceof Some<E> some)) {
            return Objects.requireNonNull(value);
        }

        return some.some;
    }

    /**
     * Creates Some Option
     *
     * @param value the value
     * @param <E>   the entry type
     * @return the Option
     * @since 1.0.0-SNAPSHOT
     */
    public static <E> Option<E> some(@NotNull final E value) {
        return new Some<>(value);
    }

    /**
     * Creates None Option
     *
     * @param <E> the entry type
     * @return the Option
     * @since 1.0.0-SNAPSHOT
     */
    public static <E> Option<E> none() {
        return new None<>();
    }

    /**
     * Represents some value
     *
     * @param <E> the entry type
     * @since 1.0.0-SNAPSHOT
     */
    public static final class Some<E> extends Option<E> {

        /**
         * The value of the option
         */
        public final E some;

        public Some(E some) {
            this.some = Objects.requireNonNull(some);
        }

        /**
         * Gets the value of the option
         *
         * @return the value of the option
         * @since 1.0.0-SNAPSHOT
         * @deprecated use the field {@link #some} instead
         */
        @Deprecated
        public E some() {
            return this.some;
        }

    }

    /**
     * Represents no value
     *
     * @param <E> arbitrary type
     * @since 1.0.0-SNAPSHOT
     */
    public static final class None<E> extends Option<E> {
        public None() {
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Option.None<?>;
        }
    }

}
