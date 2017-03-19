package samurai.command.manage;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.Commands;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.HashMap;
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
        final long enabledCommands = context.getGuild().getEnabledCommands();
        if (context.hasContent()) {
            if (context.getContent().contains("byte"))
                return FixedMessage.build("`" + Long.toBinaryString(context.getGuild().getEnabledCommands()) + "`");
            final Set<Commands> args;
            try {
                final HashMap<String, Class<? extends Command>> commandMap = CommandFactory.getCommandMap();
                args = context.getArgs().stream().map(commandMap::get).map(Class::getSimpleName).map(Commands::valueOf).collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                return FixedMessage.build("Could not find specified command");
            }
            switch (context.getKey()) {
                case "enabled":
                    return FixedMessage.build(args.stream().map(commands -> (commands.isEnabled(enabledCommands) ? "+ " : "- ") + commands.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
                case "enable":
                    String s1 = args.stream().filter(commands -> !commands.isEnabled(enabledCommands)).map(Commands::name).collect(Collectors.joining("**, **", "Enabled **", "**"));
                    context.getGuild().setEnabledCommands(args.stream().mapToLong(Commands::getValue).reduce(enabledCommands, (left, right) -> left | right));
                    return FixedMessage.build(s1);
                case "disable":
                    String s2 = args.stream().filter(commands -> commands.isEnabled(enabledCommands)).map(Commands::name).collect(Collectors.joining("**, **", "Disabled **", "**"));
                    context.getGuild().setEnabledCommands(args.stream().mapToLong(Commands::getValue).map(operand -> ~operand).reduce(enabledCommands, (left, right) -> left & right));
                    return FixedMessage.build(s2);
            }
            return null;
        } else {
            return FixedMessage.build((context.isSource() ? Arrays.stream(Commands.values()) : Commands.getVisible().stream()).map(commands -> (commands.isEnabled(enabledCommands) ? "+ " : "- ") + commands.name()).collect(Collectors.joining("\n", "```diff\n", "\n```")));
        }
    }
}
