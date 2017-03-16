package samurai.listeners;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class DiscordReactionListener extends ListenerAdapter {

    private final SamuraiDiscord samurai;

    public DiscordReactionListener(SamuraiDiscord samurai) {
        this.samurai = samurai;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            samurai.getMessageManager().onReaction(event);
        }
    }
}
