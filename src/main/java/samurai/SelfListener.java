package samurai;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author TonTL
 * @version 4.x - 2/20/2017
 */
public class SelfListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getRawContent().contains("groovy") && event.getAuthor().equals(event.getJDA().getSelfUser())) {
            event.getMessage().delete().queue();
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("SelfBot Initialized.");
    }
}
