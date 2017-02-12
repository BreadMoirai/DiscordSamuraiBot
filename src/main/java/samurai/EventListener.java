package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.data.SamuraiFile;
import samurai.duel.Game;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by TonTL on 1/28/2017.
 * Listens to events
 */
public class EventListener extends ListenerAdapter {

    long messagesSent;

    private HashMap<Long, String> prefix;
    private SamuraiController samurai;
    private HashSet<Long> gameMessageSet;
    private User self;

    private EventListener() {
        prefix = new HashMap<>();
        samurai = new SamuraiController(this);
        gameMessageSet = new HashSet<>();
        messagesSent = 0;
    }

    EventListener(OperatingSystemMXBean operatingSystemMXBean) {
        this();
        samurai.operatingSystemMXBean = operatingSystemMXBean;
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {
            long guildId = Long.parseLong(g.getId());
            if (!SamuraiFile.hasFile(guildId)) {
                SamuraiFile.writeGuildData(g);
                prefix.put(guildId, "!");
            } else {
                // wait update
                prefix.put(guildId, SamuraiFile.getPrefix(guildId));
            }
        }
        Game.samurai = event.getJDA().getSelfUser();
        self = event.getJDA().getSelfUser();
        System.out.println("Ready!" + prefix.toString());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() == self) {
            messagesSent++;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        String token = prefix.get(Long.parseLong(event.getGuild().getId()));
        String message = event.getMessage().getRawContent().trim();
        //if message begins with token ex. "!"
        if (message.startsWith(token) && message.length() > token.length() + 3) {
            message = message.substring(token.length());
            if (!message.contains(" ")) {
                samurai.action(message.toLowerCase(), event);
            } else {
                String[] argArray = message.substring(message.indexOf(" ") + 1).split("[ ]+");
                String[] argReal = new String[argArray.length];
                int j = 0;
                for (String argument : argArray) {
                    if (argument.startsWith("<@") && !argument.equalsIgnoreCase("@everyone") && !argument.equalsIgnoreCase("@here") && argument.length() != 0)
                        argReal[j++] = argument;
                }
                String key = message.substring(0, message.indexOf(" ")).toLowerCase();

                samurai.action(key, event, Arrays.copyOfRange(argReal, 0, j));
            }
        } else if (message.equalsIgnoreCase("<@270044218167132170>")) {
            samurai.action("help", event);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        long messageId = Long.parseLong(event.getMessageId());
        if (gameMessageSet.contains(messageId)) {
            samurai.updateGame(event, messageId);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        SamuraiFile.writeGuildData(event.getGuild());
    }

    //wait for jda update MemberGameUpdateEvent
    @Deprecated
    /*
    public void onUserGameUpdate(UserGameUpdateEvent event) {
        System.out.println(event.getGuild().getName() + "was playing " + (event.getPreviousGame()!= null ? event.getPreviousGame().getName() : "No Game"));

    }
    */


    @Override
    public void onShutdown(ShutdownEvent event) {
        try {
            Runtime.getRuntime().exec("cmd /c start xcopy C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\build\\resources\\main\\samurai\\data C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data /d /e /f /h /i /s /y /z /exclude:C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data\\exclude.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void updatePrefix(long guildId, String change) {
        this.prefix.put(guildId, change);
    }

    void addGame(long gameId) {
        gameMessageSet.add(gameId);
    }

    void removeGame(long gameId) {
        gameMessageSet.remove(gameId);
    }
}

