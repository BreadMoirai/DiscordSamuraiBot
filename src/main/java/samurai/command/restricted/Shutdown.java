package samurai.command.restricted;

import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.9 - 2/16/2017
 */
@Key("close")
@Source
@Creator
public class Shutdown extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        Bot.shutdown();
        return null;
    }
}
