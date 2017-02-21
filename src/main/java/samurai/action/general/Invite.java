package samurai.action.general;

import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("invite")
public class Invite extends Action {

    private static String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot&permissions=60480";

    @Override
    public SamuraiMessage buildMessage() {
        return FixedMessage.build(INVITE_URL);
    }
}
