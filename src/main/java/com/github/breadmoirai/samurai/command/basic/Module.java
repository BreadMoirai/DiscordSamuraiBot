///*    Copyright 2017 Ton Ly
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//*/
//package com.github.breadmoirai.samurai.command.basic;
//
//import com.github.breadmoirai.samurai.command.Command;
//import com.github.breadmoirai.samurai.command.CommandContext;
//import com.github.breadmoirai.samurai.command.CommandModule;
//import com.github.breadmoirai.samurai.command.annotations.Admin;
//import com.github.breadmoirai.samurai.command.annotations.Key;
//import com.github.breadmoirai.samurai.files.SamuraiStore;
//import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
//import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Key({"module", "moduleon", "moduleoff"})
//@Admin
//public class Module extends Command {
//
//    @Override
//    protected SamuraiMessage execute(CommandContext context) {
//        final SamuraiGuild samuraiGuild = context.getSamuraiGuild();
//        final long guildEnabledCommands = samuraiGuild.getModules();
//        final CommandModule[] commandModules = CommandModule.values();
//        if (context.getKey().equalsIgnoreCase("module")) {
//            if (!context.hasContent()) {
//                return FixedMessage.build(CommandModule.getVisible().stream().map(commandModule -> (commandModule.isEnabled(guildEnabledCommands) ? "+ " : "- ") + commandModule.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
//            } else {
//                return FixedMessage.build(SamuraiStore.getModuleInfo(context.getContent()));
//            }
//        } else {
//            if (context.hasContent()) {
//                final Set<CommandModule> args;
//                if (context.getContent().equalsIgnoreCase("all")) {
//                    args = new HashSet<>(CommandModule.getVisible());
//                } else {
//                    args = context.getArgs().stream().map(String::toLowerCase).filter(s -> Arrays.stream(commandModules).map(Enum::name).anyMatch(s::equals)).map(CommandModule::valueOf).filter(Objects::nonNull).collect(Collectors.toSet());
//                }
//                if (args.isEmpty())
//                    return FixedMessage.build("Could not find specified module");
//                switch (context.getKey().toLowerCase()) {
//                    case "moduleon":
//                        String s1 = args.stream().filter(commands -> !commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Enabled **", "**"));
//                        samuraiGuild.getUpdater().updateModules(args.stream().mapToLong(CommandModule::getValue).reduce(guildEnabledCommands, (left, right) -> left | right));
//                        if (args.contains(CommandModule.points)) {
//                            context.getPointTracker().enablePoints(context.getGuild());
//                        }
//                        return FixedMessage.build(s1);
//                    case "moduleoff":
//                        String s2 = args.stream().filter(commands -> commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Disabled **", "**"));
//                        samuraiGuild.getUpdater().updateModules(args.stream().mapToLong(CommandModule::getValue).map(operand -> ~operand).reduce(guildEnabledCommands, (left, right) -> left & right));
//                        return FixedMessage.build(s2);
//                }
//            }
//        }
//        return null;
//    }
//}
