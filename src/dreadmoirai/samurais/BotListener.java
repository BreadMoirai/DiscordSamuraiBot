package dreadmoirai.samurais;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.Route;

import javax.management.relation.Role;
import java.util.HashMap;

/**
 * Created by TonTL on 1/14/2017.
 *
 */
public class BotListener extends ListenerAdapter {

    public static HashMap identity;

    public BotListener() {
        identity = new HashMap();
        identity.put("[R:Shogun(267924909752188928)]","a 프로 게이머");
        identity.put("[R:Daimyo(268458081225146369)]","ur ok");
        identity.put("[R:Samurai(267925162991681547)]","cool");
        identity.put("[R:Peasant(267924616574533634)]","meh");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getRawContent().equalsIgnoreCase("who am i?")) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + identity.get(event.getMember().getRoles().toString())).queue();
        }
    }

}
