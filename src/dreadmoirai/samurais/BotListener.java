package dreadmoirai.samurais;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

/**
 * Created by TonTL on 1/14/2017.
 *
 */
public class BotListener extends ListenerAdapter {

    public static HashMap response;

    public BotListener() { //constructor
        response = new HashMap(); //hashmap for responses
        response.put("[R:Shogun(267924909752188928)]","a 프로 게이머");
        response.put("[R:Daimyo(268458081225146369)]","ur ok");
        response.put("[R:Samurai(267925162991681547)]","cool");
        response.put("[R:Peasant(267924616574533634)]","meh");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {  //when a message is sent in the channel
        String msg = event.getMessage().getRawContent().toLowerCase();
        if(msg.equals("who am i?")) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + response.get(event.getMember().getRoles().toString())).queue();
        }
    }

}
