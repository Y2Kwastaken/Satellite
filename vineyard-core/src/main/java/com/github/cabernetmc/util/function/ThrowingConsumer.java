package com.github.cabernetmc.util.function;

public interface ThrowingConsumer<T> {
    void use(T t) throws Exception;
}
