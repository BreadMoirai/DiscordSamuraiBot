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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import samurai7.core.ICommandEvent;
import samurai7.core.IModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommandEventProcessor {

    private final List<Pair<IModule, Method>> methods;
    private final HashMap<Type, IModule> moduleTypeMap;

    public CommandEventProcessor(List<IModule> modules) {
        moduleTypeMap = new HashMap<>(modules.size());
        final ArrayList<Pair<IModule, Method>> methodList = new ArrayList<>();
        for (IModule module : modules) {
            moduleTypeMap.put(module.getClass(), module);
            for (Method m : module.getClass().getDeclaredMethods())
                if (m.isAnnotationPresent(SubscribeEvent.class)
                        && m.getParameterCount() == 1
                        && !Modifier.isStatic(m.getModifiers())
                        && m.getParameterTypes()[0] == ICommandEvent.class)
                    methodList.add(new ImmutablePair<>(module, m));
        }
        methods = Collections.unmodifiableList(methodList);
    }

    private void fireCommandEvent(ICommandEvent event) {
        for (Pair<IModule, Method> pair : methods) {
            final Method method = pair.getValue();
            try {
                method.setAccessible(true);
                method.invoke(pair.getKey(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildCommandEvent(GenericGuildMessageEvent event, Message message) {
        final ICommandEvent commandEvent = new MessageReceivedCommandEvent(event, message);
    }

    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

    }




}
