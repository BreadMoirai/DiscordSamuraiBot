package dreadmoirai.samurais;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by TonTL on 1/14/2017.
 */
public class BotListener extends ListenerAdapter {

    private static HashMap roleResponses;
    private static Random rand;

    public BotListener() { //constructor
        roleResponses = new HashMap(); //hashmap for responses to who am i?
        roleResponses.put("[R:Shogun(267924909752188928)]", "a 프로 게이머");
        roleResponses.put("[R:Daimyo(268458081225146369)]", "ur ok");
        roleResponses.put("[R:Samurai(267925162991681547)]", "cool");
        roleResponses.put("[R:Peasant(267924616574533634)]", "meh");

        //Random object for rolls
        rand = new Random();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {  //when a message is sent in the channel
        String messageRecieved = event.getMessage().getRawContent().toLowerCase();
        String messageSent = "";
        if (messageRecieved.equals("who am i?")) {
            messageSent = " " + roleResponses.get(event.getMember().getRoles().toString());
        } // !roll 1-100
        else if (messageRecieved.contains("!roll")) {
            if (messageRecieved.length() > 6) {
                try {
                    int x = Integer.parseInt(messageRecieved.substring(6));
                    messageSent = " rolled " + (rand.nextInt(x) + 1) + "!";
                } catch (NumberFormatException e) {
                    messageSent = " what?";
                }
            } else {
                messageSent = " rolled " + (rand.nextInt(100) + 1) + "!";
            }
        }

        //sends message if messageSent has content
        if (messageSent.length() > 0) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + messageSent).queue();
        }
    }



}
