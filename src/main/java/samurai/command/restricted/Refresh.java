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
 * @version 5.x - 3/12/2017
 */
@Key("refresh")
@Source
@Creator
public class Refresh extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        Bot.refreshGuilds();
        return null;
    }
}
