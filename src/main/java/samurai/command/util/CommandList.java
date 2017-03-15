package samurai.command.util;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 3/8/2017
 */
@Key({"cmdall", "cmdlist"})
public class CommandList extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build(CommandFactory.keySet().stream().collect(Collectors.joining(" ", "```", "```")));
    }
}
