package samurai.action.admin;

import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Admin;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/16/2017
 */
@Key("throw")
@Admin
public class Throw extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        Bot.log(new Exception("TEST ERROR"));
        return null;
        //new FixedMessage().setMessage(new MessageBuilder().append("Thrown.").build());
    }
}
