package samurai.action.generic;

import samurai.action.Action;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @since 4.0
 */
public class InviteAction extends Action {


    @Override
    public SamuraiMessage call() {
        channel.sendMessage("https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot").queue();
        return null;
    }
}
