package samurai.action.general;

import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("invite")
public class Invite extends Action {

    @Override
    public SamuraiMessage buildMessage() {

        String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot&permissions=126016";
        if (args.size() == 1 && (args.get(0).equalsIgnoreCase("plain") || args.get(0).equalsIgnoreCase("noperm"))) {
            return FixedMessage.build(INVITE_URL.substring(0, 78));
        }
        return FixedMessage.build(INVITE_URL);
    }
}
