package samurai;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.data.SamuraiFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TonTL on 1/28/2017.
 * Listens to events
 */
public class EventListener extends ListenerAdapter {

    // wait update
    private HashMap<Long, String> prefix;
    private SamuraiController samurai;

    EventListener() {
        prefix = new HashMap<>();
        samurai = new SamuraiController(this);
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {

            long guildId = Long.parseLong(g.getId());
            if (!SamuraiFile.hasFile(guildId)) {
                SamuraiFile.writeGuild(g);
                prefix.put(guildId, "!");
            } else {
                // wait update
                prefix.put(guildId, SamuraiFile.getPrefix(guildId));
            }
        }
        System.out.println("Ready!" + prefix.toString());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String token = prefix.get(Long.parseLong(event.getGuild().getId()));
        String message = event.getMessage().getRawContent().trim();
        //if message begins with token ex. "!"
        if (message.indexOf(token) == 0 && message.length() > token.length() + 3) {
            message = message.substring(token.length());
            if (!message.contains(" ")) {
                samurai.action(message.toLowerCase(), event, null, null);
            } else {
                List<User> mentions = event.getMessage().getMentionedUsers();
                String[] argArray = message.substring(message.indexOf(" ") + 1).split(" ");
                String[] args = new String[argArray.length - mentions.size()];
                int j = 0;
                for (String anArgArray : argArray) {
                    if (anArgArray.indexOf("<@") != 0)
                        args[j++] = anArgArray;
                }
                String key = message.substring(0, message.indexOf(" ")).toLowerCase();
                if (mentions.size() == 0)
                    samurai.action(key, event, null, args);
                else
                    samurai.action(key, event, mentions, args);
            }
        } else if (message.equalsIgnoreCase("<@270044218167132170>")) {
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

    void updatePrefix(long guildId, String change) {
        this.prefix.put(guildId, change);
    }


}

