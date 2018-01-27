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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface EventActionBuilder<T extends Event> {

    EventActionBuilder<T> condition(Predicate<T> condition);

    EventActionBuilder<T> action(Consumer<T> action);

    EventActionBuilder<T> waitFor(long timeout, TimeUnit unit);

    EventActionBuilder<T> timeout(Runnable timeoutAction);

    EventActionBuilder<T> stopIf(IntPredicate afterAction);

    EventActionBuilder<T> finish(Runnable finisher);

    EventActionFuture build();

}
