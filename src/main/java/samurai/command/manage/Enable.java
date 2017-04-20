package samurai.command.manage;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandModule;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.entities.model.SGuild;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
@Key({"enabled", "enable", "disable"})
@Admin
public class Enable extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final SGuild sGuild = context.getSamuraiGuild();
        final long guildEnabledCommands = sGuild.getEnabledCommands();
        final CommandModule[] commandModules = CommandModule.values();
        if (context.hasContent()) {
            if (context.getContent().equalsIgnoreCase("byte"))
                return FixedMessage.build(String.format("`%24s`", Long.toBinaryString(sGuild.getEnabledCommands())).replace(' ', '0'));
            final Set<CommandModule> args;

            if (context.getContent().equalsIgnoreCase("all")) {
                args = (Arrays.stream(commandModules).filter(commandModule -> !commandModule.equals(CommandModule.manage))).collect(Collectors.toSet());
            } else {
                args = context.getArgs().stream().map(String::toLowerCase).filter(s -> Arrays.stream(commandModules).map(Enum::name).anyMatch(s::equals)).map(CommandModule::valueOf).filter(Objects::nonNull).collect(Collectors.toSet());
            }
            if (args.isEmpty())
                return FixedMessage.build("Could not find specified command");
            switch (context.getKey()) {
                case "enabled":
                    return FixedMessage.build(args.stream().map(commandModule -> (commandModule.isEnabled(guildEnabledCommands) ? "+ " : "- ") + commandModule.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
                case "enable":
                    String s1 = args.stream().filter(commands -> !commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Enabled **", "**"));
                    sGuild.getManager().setCommands(args.stream().mapToLong(CommandModule::getValue).reduce(guildEnabledCommands, (left, right) -> left | right));
                    return FixedMessage.build(s1);
                case "disable":
                    String s2 = args.stream().filter(commands -> commands.isEnabled(guildEnabledCommands)).map(CommandModule::name).collect(Collectors.joining("**, **", "Disabled **", "**"));
                    sGuild.getManager().setCommands(args.stream().mapToLong(CommandModule::getValue).map(operand -> ~operand).reduce(guildEnabledCommands, (left, right) -> left & right));
                    return FixedMessage.build(s2);
            }
            return null;
        } else {
            return FixedMessage.build((context.isSource() ? Arrays.stream(commandModules) : CommandModule.getVisible().stream()).map(commands -> (commands.isEnabled(guildEnabledCommands) ? "+ " : "- ") + commands.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
        }
    }
}
