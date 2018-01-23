package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;

import java.time.Instant;

interface EventActionFuture<E extends Event> {
    Instant getEndTime();

    /**
     *
     * @return {@code true} if this event is still waiting.
     */
    boolean isWaiting();

    default boolean hasRun() {
        return getRunCount() != 0;
    }

    int getRunCount();

    /**
     * returns true if the event passed the condition and ran.
     * @param event
     * @return
     */
    boolean accept(E event);

    /**
     * Stops waiting for events.
     * @return {@code true} if the action has not run yet.
     */
    boolean stop();
}
