package samurai.command.admin;

import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.data.SamuraiDatabase;

/**
 * @author TonTL
 * @version 4.9 - 2/16/2017
 */
@Key("shutdown")
@Source
@Creator
public class Shutdown extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        SamuraiDatabase.write();
        Bot.stop();
        return FixedMessage.build("See ya later, loser.");
    }
}
