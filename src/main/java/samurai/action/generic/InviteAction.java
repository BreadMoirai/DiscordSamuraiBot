package samurai.action.generic;

import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @since 4.0
 */
public class InviteAction extends Action {


    @Override
    public SamuraiMessage call() {
        return new FixedMessage()
                .setMessage(new MessageBuilder().append("https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot").build())
                .setChannelId(getChannelId());
    }
}
