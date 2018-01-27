/*
 *       Copyright 2016 John Grosh (jagrosh).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Modified by Ton Ly (BreadMoirai)
 */
package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Original class from <a href="https://github.com/JDA-Applications/JDA-Utilities/blob/master/src/main/java/com/jagrosh/jdautilities/waiter/EventWaiter.java">EventWaiter.java</a> in Github Project <a href="https://github.com/JDA-Applications/JDA-Utilities">JDA -Utilities</a> created by John Grosh (jagrosh).
 * Modified by Ton Ly (BreadMoirai)
 * <p>
 * <p>The EventWaiter is capable of handling specialized forms of {@link net.dv8tion.jda.core.events.Event Event}
 * that must meet criteria not normally specifiable without implementation of an {@link net.dv8tion.jda.core.hooks.EventListener EventListener}.
 * <p>
 * The change is that the test predicate is optional and the action is a predicate which only stops receiving events of the specified type when it returns {@code true}
 *
 * @author John Grosh (jagrosh); Modified by Ton Ly (BreadMoirai)
 */
public class EventWaiterB implements EventListener {

    private final Map<Class<?>, List<EventAction>> waitingEvents;

    private final ScheduledExecutorService threadpool;

    /**
     * Constructs an empty EventWaiter.
     */
    public EventWaiterB() {
        waitingEvents = new HashMap<>();
        threadpool = Executors.newSingleThreadScheduledExecutor();
    }

    private <T extends Event> List<EventAction> getActions(Class<T> eventType) {
        final List<EventAction> list = waitingEvents.get(eventType);
        if (list != null) {
            return list;
        }
        final List<EventAction> newList = new ArrayList<>();
        waitingEvents.put(eventType, newList);
        return newList;
    }

    /**
     * Waits an indefinite amount of time for an {@link net.dv8tion.jda.core.events.Event Event} on which the provided {@link java.util.function.Predicate Predicate} would return {@code true}.
     * <p>
     * <p>When this occurs, the provided {@link java.util.function.Consumer Consumer} will accept and
     * handle using the same {@link net.dv8tion.jda.core.events.Event Event}.
     *
     * @param <T>
     *         The type of Event to wait for
     * @param eventType
     *         The {@link java.lang.Class} of the {@link net.dv8tion.jda.core.events.Event Event} to wait for
     * @param condition
     *         A {@link java.util.function.Predicate Predicate}. Should return {@code true} when the {@link net.dv8tion.jda.core.events.Event Event} should be passed to the {@code action}, else {@code false} if the EventWaiter continue waiting event.
     * @param action
     *         This is a Consumer that is run when the {@code condition} is satisfied.
     */
    public <T extends Event> EventActionFuture waitForEvent(Class<T> eventType, Predicate<T> condition, Consumer<T> action) {
        final CompletableFuture<Object> future = new CompletableFuture<>();


        final List<EventActionFuture> actions = getActions(eventType);
        final IndefiniteEventAction<T> f = new IndefiniteEventAction<>(condition, action, actions);
        actions.add(f);
        return f;
    }

    /**
     * Waits a predetermined amount of time for an {@link net.dv8tion.jda.core.events.Event Event} that
     * returns {@code true} when tested with the provided {@link java.util.function.Predicate Predicate} {@code condition}.
     * Then the Event is passed to the provided {@link java.util.function.Predicate Predicate} {@code action}. If {@code action} returns {@code true}, the action will be removed. If the {@code action} returns {@code false}, it will continue to wait for additional Events.
     * <p>
     * <p>Once the this times out, if {@code condition} has ever returned {@code true}, the {@code timeoutAction} will not be run. If {@code condition} has never returned {@code true}, the {@code timeoutAction} will be run.
     *
     * @param <T>
     *         The type of Event to wait for
     * @param eventType
     *         The {@link java.lang.Class} of the Event to wait for
     * @param condition
     *         The Predicate that tests the Event
     * @param action
     *         The Consumer that is run when {@code condition} returns {@code true}
     * @param timeout
     *         The maximum amount of time to wait for
     * @param unit
     *         The {@link java.util.concurrent.TimeUnit TimeUnit} measurement of the timeout
     * @param timeoutAction
     *         The Runnable to run if the time runs out before a correct Event is thrown
     */
    public <T extends Event> void waitForEvent(Class<T> eventType, Predicate<T> condition, Consumer<T> action, long timeout, TimeUnit unit, Runnable timeoutAction) {


    }

    public <T extends Event> EventActionBuilder actionBuilder(Class<T> eventClass) {
        return new EventActionBuilderImpl(this, threadpool);
    }

    void addActionFuture(Class<? extends Event> eventClass, EventAction<?> action) {
        getActions(eventClass).add(action);
    }

    @SubscribeEvent
    @Override
    public final void onEvent(Event event) {
        Class c = event.getClass();
        while (c != Object.class) {
            if (waitingEvents.containsKey(c)) {
                List<EventAction<?>> list = waitingEvents.get(c);
                list.removeAll(list.stream()
                        .filter(i -> i.acceptEvent(event))
                        .collect(Collectors.toList()));
            }
            if (event instanceof ShutdownEvent) {
                threadpool.shutdown();
            }
            c = c.getSuperclass();
        }
    }


}