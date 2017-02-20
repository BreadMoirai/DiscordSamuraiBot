package samurai.action.admin;

import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Creator;
import samurai.annotations.Key;
import samurai.annotations.Source;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/16/2017
 */
@Key("throw")
@Source
@Creator
public class Throw extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        Bot.logError(new Exception("TEST ERROR"));
        return null;
    }
}
