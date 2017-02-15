package samurai.action.generic;

import samurai.action.Action;
import samurai.message.SamuraiMessage;
import samurai.message.duel.ConnectFour;

/**
 * @author TonTL
 * @since 4.0
 */
public class DuelAction extends Action {

    @Override
    public SamuraiMessage call() {
        ConnectFour samuraiMessage = new ConnectFour(author.getUser());
 /*       if (mentions.size() == 1) {
            samuraiMessage.begin(new Reaction()
                    .setUser(mentions.get(0)));
        }*/
        return samuraiMessage.setChannelId(channelId);
    }
}
