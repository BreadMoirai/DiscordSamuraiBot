/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.plugins.waiter;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.utils.Checks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class EventActionBuilderImpl<T extends Event> implements EventActionBuilder<T> {

    private final EventWaiterB eventWaiterB;
    private final ScheduledExecutorService threadpool;

    private Predicate<T> condition = o -> true;
    private Consumer<T> action;
    private long timeout;
    private TimeUnit unit;
    private Runnable timeoutAction;
    private IntPredicate afterAction = i -> true;
    private Runnable finisher;

    public EventActionBuilderImpl(EventWaiterB eventWaiterB, ScheduledExecutorService threadpool) {
        this.eventWaiterB = eventWaiterB;
        this.threadpool = threadpool;
    }

    @Override
    public EventActionBuilder<T> condition(Predicate<T> condition) {
        Checks.notNull(condition, "condition");
        this.condition = condition;
        return this;
    }

    @Override
    public EventActionBuilder<T> action(Consumer<T> action) {
        Checks.notNull(action, "action");
        this.action = action;
        return this;
    }

    @Override
    public EventActionBuilder<T> waitFor(long timeout, TimeUnit unit) {
        Checks.notNull(unit, "unit");
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    public EventActionBuilder<T> timeout(Runnable timeoutAction) {
        Checks.notNull(timeoutAction, "timeoutAction");
        this.timeoutAction = timeoutAction;
        return this;
    }

    @Override
    public EventActionBuilder<T> stopIf(IntPredicate afterAction) {
        Checks.notNull(afterAction, "afterAction");
        this.afterAction = afterAction;
        return this;
    }

    @Override
    public EventActionBuilder<T> finish(Runnable finisher) {
        Checks.notNull(finisher, "finisher");
        this.finisher = finisher;
        return this;
    }

    @Override
    public EventActionFuture build() {
        if (unit == null) {
            new IndefiniteEventAction<>();
        } else {

        }

    }
}
