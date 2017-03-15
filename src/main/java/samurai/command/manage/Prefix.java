package samurai.command.manage;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("prefix")
@Admin
public class Prefix extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        final List<String> args = context.getArgs();
        if (args.size() != 1)
            return FixedMessage.build("Invalid Argument. Please provide a single word");
        context.getGuild().setPrefix(args.get(0));
        return FixedMessage.build(String.format("Prefix successfully set to `%s`", args.get(0)));
    }
}
