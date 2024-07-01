package io.github.cabernetmc.execution;

import org.jetbrains.annotations.NotNull;

public class ExecutionError extends Error {

    public ExecutionError(@NotNull final String message) {
        super(message);
    }

    public ExecutionError(@NotNull final Throwable cause) {
        super(cause);
    }

}
