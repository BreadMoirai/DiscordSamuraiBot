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

    private static String INVITE_URL = "https://discordapp.com/oauth2/authorize?client_id=270044218167132170&scope=bot&permissions=60480";

    @Override
    public SamuraiMessage buildMessage() {
        FixedMessage fixedMessage = new FixedMessage();
        fixedMessage.setMessage(new MessageBuilder().append(INVITE_URL).build());
        return fixedMessage;
    }
}
