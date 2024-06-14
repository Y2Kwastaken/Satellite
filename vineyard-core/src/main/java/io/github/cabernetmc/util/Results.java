package io.github.cabernetmc.util;

/**
 * Similar to {@link Runnable} however each run function returns some result that can be used
 *
 * @param <R> the success return type
 * @param <E> the exception return type
 */
public interface Results<R, E extends Exception> {
    Result<R, E> run();
}
