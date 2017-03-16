package samurai.events;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * @author TonTL
 * @version 3/15/2017
 */
public interface ReactionListener extends SamuraiListener {
    void onReaction(MessageReactionAddEvent event);
}
