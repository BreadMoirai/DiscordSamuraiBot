package samurai.command.admin;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 5.x - 3/12/2017
 */
@Key("refresh")
public class Refresh extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        //Bot.refreshGuilds(); //todo
        return null;
    }
}
