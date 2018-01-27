package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;

import java.time.Instant;

@FunctionalInterface
interface EventAction<T extends Event> {

    default boolean acceptEvent(Event event) {
        @SuppressWarnings("unchecked")
        final T t = (T) event;
        return accept(t);
    }

    boolean accept(T event);

}
