package samurai;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.data.SamuraiFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TonTL on 1/28/2017.
 * Listens to events
 */
public class EventListener extends ListenerAdapter {

    // wait update
    private HashMap<String, String> prefix;
    private SamuraiController samurai;

    EventListener() {
        prefix = new HashMap<>();
        samurai = new SamuraiController();
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {
            if (!SamuraiFile.hasFile(Long.parseLong(g.getId()))) {
                SamuraiFile.writeGuild(g);
                prefix.put(g.getId(), "!");
            } else {
                // wait update
                prefix.put(g.getId(), SamuraiFile.getPrefix(Long.parseLong(g.getId())));
            }
        }
        System.out.println("Ready!" + prefix.toString());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String token = prefix.get(event.getGuild().getId());
        String message = event.getMessage().getRawContent().trim();
        //if message begins with token ex. "!"
        if (message.indexOf(token) == 0 && message.length() > token.length() + 3) {
            message = message.substring(token.length());
            if (!message.contains(" ")) {
                samurai.action(message.toLowerCase(), event, null, null);
            } else {
                List<String> args = Arrays.asList(message.substring(message.indexOf(" ") + 1).split(" "));
                for (String argument : args) {
                    if (argument.indexOf("<@") == 0) {
                        args.remove(argument);
                    }
                }
                String key = message.substring(0, message.indexOf(" ")).toLowerCase();
                List<User> mentions = event.getMessage().getMentionedUsers();
                if (mentions.size() == 0)
                    samurai.action(key, event, null, args);
                else
                    samurai.action(key, event, mentions, args);
            }
        } else if (message.equalsIgnoreCase("<@&270044218167132170>")) {
            samurai.action("help", event, null, null);
        }
    }


    @Override
    public void onShutdown(ShutdownEvent event) {
        try {
            Runtime.getRuntime().exec("cmd /c start xcopy /s/y/v C:\\Users\\TonTL\\Desktop\\DiscordSamuraiBot\\build\\resources\\main\\samurai\\data\\guild C:\\Users\\TonTL\\Desktop\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data\\guild");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

