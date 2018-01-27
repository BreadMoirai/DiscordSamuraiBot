package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class IndefiniteEventAction<E extends net.dv8tion.jda.core.events.Event> implements EventAction<E> {
    private final Predicate<E> condition;
    private final Consumer<E> action;
    private final List<EventActionFuture> eventActions;

    private boolean isWaiting = true;
    private boolean hasRun = false;

    public IndefiniteEventAction(Predicate<E> condition, Consumer<E> action, List<EventActionFuture> eventActions) {
        this.condition = condition;
        this.action = action;
        this.eventActions = eventActions;
    }

    @Override
    public Instant getEndTime() {
        return null;
    }

    @Override
    public boolean hasRun() {
        return hasRun;
    }

    @Override
    public boolean isWaiting() {
        return isWaiting;
    }

    @Override
    public int getRunCount() {
        return hasRun ? 1 : 0;
    }

    @Override
    public void accept(Event event) {
        @SuppressWarnings("unchecked")
        final E e = (E) event;
        if (condition.test(e)) {
            action.accept(e);
            this.hasRun = true;
            stop();
        }
    }

    @Override
    public boolean stop() {
        isWaiting = false;
        return eventActions.remove(this);
    }

}
