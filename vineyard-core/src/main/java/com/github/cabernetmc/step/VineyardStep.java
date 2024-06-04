package com.github.cabernetmc.step;

/**
 * A Step within the Vineyard compilation process
 * <p>
 * Currently this interface is purely aesthetic to enforce possible changes down the line. For now the steps Vineyard
 * takes are hard coded and based upon a builder class, however, using this interface allows possible expansions in the
 * future.
 *
 * @since 1.0.0-SNAPSHOT
 */
public interface VineyardStep extends Runnable {

    void verifyCompleted();
}
