package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedEventActionFuture<E extends Event> implements EventActionFuture {
    private final Predicate<E> condition;
    private final Consumer<E> runner;
    private final ScheduledFuture timeout;
    private final Runnable timeoutAction;
    private final Instant endTime;
    private final List<EventActionFuture> eventActions;


    private boolean finished = false;
    private boolean hasRun = false;

    public TimedEventActionFuture(Predicate<E> condition, Consumer<E> runner, Runnable timeoutAction, Instant endTime, List<EventActionFuture> eventActions, ScheduledExecutorService executor) {
        this.condition = condition;
        this.runner = runner;
        this.timeoutAction = timeoutAction;
        this.endTime = endTime;
        this.eventActions = eventActions;
        this.timeout = executor.schedule(this::timeout, Instant.now().until(endTime, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    @Override
    public boolean hasRun() {
        return hasRun;
    }

    @Override
    public boolean isWaiting() {
        return !finished;
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
            hasRun = true;
            runner.accept(e);
            stop();
        }
    }

    @Override
    public boolean stop() {
        finished = true;
        eventActions.remove(this);
        return timeout.cancel(false);
    }

    private void timeout() {
        finished = true;
        eventActions.remove(this);
        timeoutAction.run();
    }

}
