package io.github.cabernetmc.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a result of a method. That can return either an expected a return value or an exception as a value.
 * <p>
 * Result is useful in cases where error propagation or nice handling of errors is required without interrupting control
 * flow of a program
 *
 * @param <R> The resulting type
 * @param <E> The exception type
 * @since 1.0.0-SNAPSHOT
 */
public sealed class Result<R, E extends Exception> {

    /**
     * Creates a new result that was a success
     *
     * @param result the result
     * @param <R>    the result type
     * @param <E>    the exception type
     * @return a new successful result
     */
    public static <R, E extends Exception> Result<R, E> success(@NotNull final R result) {
        return new Success<>(result);
    }

    /**
     * Creates a new result that was a failure
     *
     * @param exception the exception
     * @param <R>       the result type
     * @param <E>       the exception type
     * @return a new failure result
     */
    public static <R, E extends Exception> Result<R, E> failure(@NotNull final E exception) {
        return new Failure<>(exception);
    }

    /**
     * Represents a failure within a result
     *
     * @param <E> the exception type
     * @since 1.0.0-SNAPSHOT
     */
    public static final class Failure<R, E extends Exception> extends Result<R, E> {
        /**
         * The resulting exception
         *
         * @since 1.0.0-SNAPSHOT
         */
        public final E exception;

        /**
         * Creates a new failure
         *
         * @param exception the exception
         */
        Failure(@NotNull final E exception) {
            this.exception = Objects.requireNonNull(exception);
        }
    }

    /**
     * Represents a success within a result
     *
     * @param <R> The resulting type
     * @since 1.0.0-SNAPSHOT
     */
    public static final class Success<R, E extends Exception> extends Result<R, E> {
        /**
         * The resulting value
         *
         * @since 1.0.0-SNAPSHOT
         */
        public final R result;

        /**
         * Creates a new result
         *
         * @param result the result
         * @since 1.0.0-SNAPSHOT
         */
        Success(@NotNull final R result) {
            this.result = Objects.requireNonNull(result);
        }
    }

}
