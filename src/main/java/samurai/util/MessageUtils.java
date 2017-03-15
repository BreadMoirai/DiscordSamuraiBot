package samurai.util;

import net.dv8tion.jda.core.entities.Message;
import samurai.events.ReactionEvent;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class MessageUtils {
    public static void removeReaction(Message message, ReactionEvent event) {
        message.getReactions().stream().filter(messageReaction -> messageReaction.getEmote().getName().equals(event.getName())).findFirst().ifPresent(messageReaction -> messageReaction.removeReaction(event.getUser()).queue());
    }
}
