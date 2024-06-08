package com.github.cabernetmc.execution.data;

import com.github.cabernetmc.execution.AbstractVineyardExecution;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecutionOrder {

    private final List<AbstractVineyardExecution> orderedExecutions = new ArrayList<>();
    private int index = 0;

    public ExecutionOrder(List<AbstractVineyardExecution> orderedExecutions) {
        for (final AbstractVineyardExecution orderedExecution : orderedExecutions) {
            insert(orderedExecution);
        }
    }

    public void runNextExecution() {
        if (this.orderedExecutions.isEmpty() || index >= orderedExecutions.size()) return;
        final AbstractVineyardExecution next = this.orderedExecutions.get(index);
        if (next.hasDependencies()) {
            System.out.println(next);
            final Set<Class<? extends AbstractVineyardExecution>> dependencies = next.getDependencies();
            next.init(orderedExecutions.stream().filter((e) -> dependencies.contains(e.getClass())).collect(Collectors.toMap(AbstractVineyardExecution::getClass, (execution) -> execution)));
        }
        next.execute();
        next.verifyExecution();
        this.index++;
    }

    public boolean hasNextExecution() {
        return index < this.orderedExecutions.size();
    }

    public void insert(@NotNull final AbstractVineyardExecution execution) throws IllegalStateException {
        if (this.orderedExecutions.isEmpty()) {
            this.orderedExecutions.add(execution);
            return;
        }

        if (!execution.hasDependencies()) {
            this.orderedExecutions.add(execution);
            return;
        }

        final var dependencies = new HashSet<>(execution.getDependencies());
        final var currentExecutions = new ArrayList<>(this.orderedExecutions);
        for (final AbstractVineyardExecution currentExecution : currentExecutions) {
            if (dependencies.remove(currentExecution.getClass()) && dependencies.isEmpty()) {
                this.orderedExecutions.add(execution);
                return;
            }
        }

        throw new IllegalStateException("A Execution has been inserted that does not have a dependency yet satisfied. Please ensure all dependencies for this insertion are inserted prior to this execution");
    }

    public Map<Class<? extends AbstractVineyardExecution>, AbstractVineyardExecution> getAllRanExecutions() {
        final Map<Class<? extends AbstractVineyardExecution>, AbstractVineyardExecution> ran = new HashMap<>();
        for (int i = 0; i < this.index; i++) {
            final var execution = orderedExecutions.get(i);
            ran.put(execution.getClass(), execution);
        }
        return ran;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder().append("[\n");
        for (final AbstractVineyardExecution orderedExecution : this.orderedExecutions) {
            builder.append(orderedExecution.getClass().getSimpleName()).append("\n");
        }
        return builder.append("]").toString();
    }
}
