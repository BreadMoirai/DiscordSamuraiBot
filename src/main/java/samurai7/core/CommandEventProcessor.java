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
package samurai7.core;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import samurai7.core.response.ResponseHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class CommandEventProcessor {

    private final List<Pair<Object, Method>> methods;
    private final Map<Type, IModule> moduleTypeMap;
    private final List<IModule> modules;
    private final Map<String, Class<? extends ICommand>> commandMap;

    private final ResponseHandler responseHandler;

    private final Predicate<Message> preProcessPredicate;
    private final Predicate<ICommand> postProcessPredicate;


    public CommandEventProcessor(List<IModule> modules, ResponseHandler responseHandler, CommandProcessorConfiguration configuration) {
        this.modules = modules;
        this.responseHandler = responseHandler;
        this.commandMap = configuration.getCommandMap();
        this.preProcessPredicate = configuration.getPreProcessPredicate();
        this.postProcessPredicate = configuration.getPostProcessPredicate();
        final HashMap<Type, IModule> typeMap = new HashMap<>(modules.size());
        final ArrayList<Pair<Object, Method>> methodList = new ArrayList<>();
        for (IModule module : modules) {
            typeMap.put(module.getClass(), module);
            for (Method m : module.getClass().getDeclaredMethods())
                if (m.isAnnotationPresent(SubscribeEvent.class)
                        && m.getParameterCount() == 1
                        && !Modifier.isStatic(m.getModifiers())
                        && m.getParameterTypes()[0] == ICommandEvent.class)
                    methodList.add(new ImmutablePair<>(module, m));
        }
        methodList.add(new ImmutablePair<>(responseHandler, ))
        this.moduleTypeMap = Collections.unmodifiableMap(typeMap);
        methods = Collections.unmodifiableList(methodList);
    }

    private void processEvent(ICommandEvent event) {
        ICommand command = null;
        final String key = event.getKey().toLowerCase();
        try {
            command = commandMap.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (command == null) {
            for (IModule module : modules) {
                command = module.getCommand(key);
                if (command != null) break;
            }
        }
        if (command == null) return;
        command.setEvent(event);
        command.setModules(moduleTypeMap);

        if (postProcessPredicate.test(command)) {
            CompletableFuture.supplyAsync(command::call).thenAcceptAsync(responseOptional -> responseOptional.ifPresent(responseHandler::submit));
            CompletableFuture.runAsync(() -> fireCommandEvent(event));
        }
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

    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (preProcessPredicate.test(event.getMessage()))
            processEvent(new MessageReceivedCommandEvent(event, event.getMessage()));
    }

    @SubscribeEvent
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (preProcessPredicate.test(event.getMessage()))
            processEvent(new MessageReceivedCommandEvent(event, event.getMessage()));
    }
}
