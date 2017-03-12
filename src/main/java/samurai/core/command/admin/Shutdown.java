package samurai.core.command.admin;

import samurai.core.Bot;
import samurai.core.command.Command;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;
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
    protected SamuraiMessage buildMessage() {
        SamuraiDatabase.write();
        Bot.stop();
        return FixedMessage.build("See ya later, loser.");
    }
}
