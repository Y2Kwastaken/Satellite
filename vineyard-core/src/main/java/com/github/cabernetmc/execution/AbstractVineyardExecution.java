package com.github.cabernetmc.execution;

import com.github.cabernetmc.execution.data.ExecutionOrder;
import com.github.cabernetmc.execution.utility.api.VineyardExecutionRequestHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public abstract class AbstractVineyardExecution {

    protected final VineyardExecutionSettings settings;
    protected final VineyardExecutionRequestHelper requestHelper;
    protected final Map<Class<? extends AbstractVineyardExecution>, AbstractVineyardExecution> dependencies;
    protected final Set<Class<? extends AbstractVineyardExecution>> dependencyClasses;

    /**
     * Creates a new AbstractVineyardExecutor
     *
     * @param settings      the execution settings
     * @param requestHelper the request helper
     */
    public AbstractVineyardExecution(@NotNull final VineyardExecutionSettings settings, @NotNull final VineyardExecutionRequestHelper requestHelper) {
        this.settings = settings;
        this.requestHelper = requestHelper;
        this.dependencyClasses = new HashSet<>();
        this.dependencies = new HashMap<>();
    }

    /**
     * Initializes the AbstractVineyardExecution with its required dependencies
     *
     * @param dependencies the dependencies to initialize this execution with
     */
    public final void init(Map<Class<? extends AbstractVineyardExecution>, AbstractVineyardExecution> dependencies) {
        if (!this.dependencies.isEmpty()) {
            throw new IllegalStateException("Can not initialize the %s execution twice".formatted(getClass().getSimpleName()));
        }
        this.dependencies.putAll(dependencies);
    }

    /**
     * Gets a copied set of all of the dependencies of this execution
     *
     * @return a copy of all dependencies
     */
    public final Set<Class<? extends AbstractVineyardExecution>> getDependencies() {
        return new HashSet<>(this.dependencyClasses);
    }

    /**
     * Checks whether or not that this Execution has dependencies
     * <p>
     * If no dependencies are provided execution order within a {@link ExecutionOrder} should be considered somewhat
     * arbitrary
     *
     * @return true given this execution has dependencies
     */
    public final boolean hasDependencies() {
        return !this.dependencyClasses.isEmpty();
    }

    /**
     * Executes the execution
     */
    public abstract void execute();

    /**
     * Verifies the execution executed as expected
     */
    public abstract void verifyExecution();

}
