package samurai.action.generic;

import net.dv8tion.jda.core.entities.Message;
import samurai.action.Action;
import samurai.action.Reaction;
import samurai.persistent.SamuraiMessage;
import samurai.persistent.duel.ConnectFour;

/**
 * @author TonTL
 * @since 4.0
 */
public class DuelAction extends Action {

    @Override
    public SamuraiMessage call() {
        ConnectFour samuraiMessage = new ConnectFour(author.getUser());
        if (mentions == null) {
            Message message = channel.sendMessage("Who is willing to engage " + author.getAsMention() + " in a battle of life and death?").complete();
            message.addReaction("⚔").queue();
            samuraiMessage.setMessageId(message.getId());
        } else if (mentions.size() == 1) {
            Message message = channel.sendMessage("Creating game...").complete();
            samuraiMessage.setMessageId(message.getId());
            samuraiMessage.begin(new Reaction()
                    .setChannel(channel)
                    .setUser(mentions.get(0)));
        } else {
            channel.getMessageById(String.valueOf(messageId)).queue(msg -> msg.addReaction("❌").queue());
            return null;
        }
        return samuraiMessage;
    }
}
