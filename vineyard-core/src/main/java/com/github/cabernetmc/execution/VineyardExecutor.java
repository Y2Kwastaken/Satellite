package com.github.cabernetmc.execution;

import com.github.cabernetmc.execution.data.ExecutionOrder;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes a list of VineyardExecutions and their settings and helpers
 */
public class VineyardExecutor {

    private final ExecutionOrder executions;
    private final VineyardExecutionSettings settings;

    private VineyardExecutor(ExecutionOrder executions, VineyardExecutionSettings settings) {
        this.executions = executions;
        this.settings = settings;
    }

    public void runAll() {
        while (this.executions.hasNextExecution()) {
            this.executions.runNextExecution();
        }
    }

    public ExecutionOrder getExecutions() {
        return executions;
    }

    public VineyardExecutionSettings getSettings() {
        return settings;
    }

    public static class Builder {

        private final List<Class<? extends AbstractVineyardExecution>> executions = new ArrayList<>();
        private VineyardExecutionSettings settings;
        private VineyardExecutionRequestHelper requestHelper;

        public Builder() {
        }

        public Builder execution(Class<? extends AbstractVineyardExecution> execution) {
            this.executions.add(execution);
            return this;
        }

        public Builder settings(@NotNull final VineyardExecutionSettings settings) {
            this.settings = Objects.requireNonNull(settings);
            return this;
        }

        public Builder requestHelper(@NotNull final VineyardExecutionRequestHelper requestHelper) {
            this.requestHelper = Objects.requireNonNull(requestHelper);
            return this;
        }

        public VineyardExecutor build() {
            verify();

            final ExecutionOrder executionOrder = new ExecutionOrder(new ArrayList<>());
            try {
                for (final Class<? extends AbstractVineyardExecution> execution : this.executions) {
                    final Constructor<? extends AbstractVineyardExecution> constructor = execution.getConstructor(VineyardExecutionSettings.class, VineyardExecutionRequestHelper.class);
                    executionOrder.insert(constructor.newInstance(this.settings, this.requestHelper));
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            return new VineyardExecutor(executionOrder, this.settings);
        }

        private void verify() {
            assert this.settings != null;
            assert this.requestHelper != null;
            assert !this.executions.isEmpty();
        }
    }
}
