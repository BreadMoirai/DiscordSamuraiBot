package com.github.breadmoirai.samurai.plugins.waiter;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventActionFutureImpl<E extends net.dv8tion.jda.core.events.Event> implements EventActionFuture<E> {
    private Predicate<E> condition;
    private Consumer<E> runner;
    private int runCount;

    @Override
    public Instant getEndTime() {
        return null;
    }

    @Override
    public boolean isWaiting() {
        return false;
    }

    @Override
    public int getRunCount() {
        return 0;
    }

    @Override
    public boolean accept(E event) {
        if (condition.test(event)) {
            runner.accept(event);
            runCount++;
            return true;
        }
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
