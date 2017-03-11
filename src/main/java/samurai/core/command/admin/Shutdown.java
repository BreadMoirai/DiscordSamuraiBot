package samurai.core.command.admin;

import samurai.core.Bot;
import samurai.core.command.Command;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.data.SamuraiDatabase;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.4 - 2/16/2017
 */
@Key("shutdown")
@Source
@Creator
public class Shutdown extends Command {

    @Override
    protected SamuraiMessage buildMessage() {
        SamuraiDatabase.write();
        Bot.stop();
        return FixedMessage.build("See ya later, loser.");
    }
}
