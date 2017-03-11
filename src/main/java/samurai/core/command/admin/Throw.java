package samurai.core.command.admin;

import samurai.core.Bot;
import samurai.core.command.Command;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.entities.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/16/2017
 */
@Key("throw")
@Source
@Creator
public class Throw extends Command {

    @Override
    protected SamuraiMessage buildMessage() {
        Bot.logError(new Exception("TEST ERROR"));
        return null;
    }
}
