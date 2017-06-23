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
import org.apache.commons.lang3.reflect.TypeUtils;
import org.reflections.Reflections;
import samurai.command.annotations.Key;
import samurai7.util.DuplicateCommandKeyError;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class CommandProcessorConfiguration {
    Predicate<Message> preProcessPredicate;
    Predicate<ICommand> postProcessPredicate;

    Map<String, Class<? extends ICommand>> commandMap;


    public CommandProcessorConfiguration addPreProcessPredicate(Predicate<Message> predicate) {
        if (preProcessPredicate == null) {
            preProcessPredicate = predicate;
        } else preProcessPredicate = preProcessPredicate.and(predicate);
        return this;
    }

    public CommandProcessorConfiguration addPostProcessPredicate(Predicate<ICommand> predicate) {
        if (postProcessPredicate == null) {
            postProcessPredicate = predicate;
        } else postProcessPredicate = postProcessPredicate.and(predicate);
        return this;
    }

    public CommandProcessorConfiguration registerCommand(Class<? extends ICommand> commandClass, String... keys) {
        for (String key : keys) {
            if (key == null || keys.length == 0) {
                System.err.println("No key found for " + commandClass.getSimpleName());
                continue;
            }
            final String key1 = key.toLowerCase();
            if (commandMap.containsKey(key1))
                throw new DuplicateCommandKeyError(key1, commandMap.get(key1), commandClass);
            else {
                commandMap.put(key, commandClass);
                System.out.println(("\"" + key + "\" mapped to " + commandClass.getSimpleName() + " in Module " + TypeUtils.getTypeArguments(commandClass, Command.class).get(Command.class.getTypeParameters()[0]).getTypeName()));
            }
        }
        return this;
    }

    public CommandProcessorConfiguration registerCommand(Class<? extends Command> commandClass) {
        if (commandClass.isAnnotationPresent(Key.class)) {
            final String[] keyArray = commandClass.getAnnotation(Key.class).value();
            registerCommand(commandClass, keyArray);
        } else {
            System.err.println("No key found for " + commandClass.getSimpleName());
        }
        return this;
    }

    public CommandProcessorConfiguration registerCommand(String commandPackage) {
        Reflections reflections = new Reflections(commandPackage);
        Set<Class<? extends ICommand>> classes = reflections.getSubTypesOf(ICommand.class);
        for (Class<? extends ICommand> commandClass : classes) {
            registerCommand(commandClass);
        }
        return this;
    }

    Map<String, Class<? extends ICommand>> getCommandMap() {
        return commandMap;
    }

    Predicate<Message> getPreProcessPredicate() {
        return preProcessPredicate;
    }

    Predicate<ICommand> getPostProcessPredicate() {
        return postProcessPredicate;
    }
}
