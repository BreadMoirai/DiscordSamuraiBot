/*
 *       Copyright 2017 Ton Ly
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
 */
package samurai7.core.impl;

import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import samurai7.core.ICommandEvent;
import samurai7.core.IModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class CommandEventProcessor {

    private List<Pair<IModule, Method>> methods;

    public CommandEventProcessor(List<IModule> modules) {
        for (IModule module : modules)
            for (Method m : module.getClass().getDeclaredMethods())
                if (m.isAnnotationPresent(SubscribeEvent.class)
                        && m.getParameterCount() == 1
                        && !Modifier.isStatic(m.getModifiers())
                        && m.getParameterTypes()[0] == ICommandEvent.class)
                    methods.add(new ImmutablePair<>(module, m));
    }

    private void fireCommandEvent(ICommandEvent event) {
        for (Pair<IModule, Method> pair : methods) {
            final Method method = pair.getValue();
            method.setAccessible(true);
            try {
                method.invoke(pair.getKey(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }



}
