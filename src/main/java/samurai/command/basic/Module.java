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
package samurai.command.basic;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandModule;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.database.objects.SamuraiGuild;
import samurai.files.SamuraiStore;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Key({"module", "enable", "disable"})
@Admin
public class Module extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final SamuraiGuild samuraiGuild = context.getSamuraiGuild();
        final long guildEnabledCommands = samuraiGuild.getModules();
        final CommandModule[] commandModules = CommandModule.values();
        if (context.getKey().equalsIgnoreCase("module")) {
            if (!context.hasContent()) {
                return FixedMessage.build(CommandModule.getVisible().stream().filter(commandModule -> commandModule != CommandModule.basic).map(commandModule -> (commandModule.isEnabled(guildEnabledCommands) ? "+ " : "- ") + commandModule.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
            } else {
                return FixedMessage.build(SamuraiStore.getModuleInfo(context.getContent()));
            }
        } else {
            if (context.hasContent()) {
                final Set<CommandModule> args;
                if (context.getContent().equalsIgnoreCase("all")) {
                    args = (Arrays.stream(commandModules).filter(commandModule -> !commandModule.equals(CommandModule.manage))).collect(Collectors.toSet());
                } else {
                    args = context.getArgs().stream().map(String::toLowerCase).filter(s -> Arrays.stream(commandModules).map(Enum::name).anyMatch(s::equals)).map(CommandModule::valueOf).filter(Objects::nonNull).collect(Collectors.toSet());
                }
                if (args.isEmpty())
                    return FixedMessage.build("Could not find specified command");
                switch (context.getKey()) {
                    case "enable":
                        String s1 = args.stream().filter(commands -> !commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Enabled **", "**"));
                        samuraiGuild.getUpdater().updateModules(args.stream().mapToLong(CommandModule::getValue).reduce(guildEnabledCommands, (left, right) -> left | right));
                        return FixedMessage.build(s1);
                    case "disable":
                        String s2 = args.stream().filter(commands -> commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Disabled **", "**"));
                        samuraiGuild.getUpdater().updateModules(args.stream().mapToLong(CommandModule::getValue).map(operand -> ~operand).reduce(guildEnabledCommands, (left, right) -> left & right));
                        return FixedMessage.build(s2);
                }
            }
        }
        return null;
    }
}
