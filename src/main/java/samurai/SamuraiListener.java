package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.action.Action;
import samurai.action.general.Help;
import samurai.data.SamuraiFile;
import samurai.message.modifier.Reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener for SamuraiBot
 * This class listens to events from discord, takes the required information by building the appropriate action and passing it to SamuraiController#execute
 *
 * @author TonTL
 * @version 4.0 - 2/16/2017
 * @see SamuraiController
 */
public class SamuraiListener extends ListenerAdapter {
    public static final AtomicInteger messagesSent = new AtomicInteger(0);
    private final HashMap<Long, String> prefixMap;
    private SamuraiController samurai;


    SamuraiListener() {
        prefixMap = new HashMap<>();
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {
            long guildId = Long.parseLong(g.getId());
            if (!SamuraiFile.hasFile(guildId)) {
                prefixMap.put(guildId, "!");
            } else {
                prefixMap.put(guildId, SamuraiFile.getPrefix(guildId));
            }

            if (guildId == 233097800722808832L) {
                SamuraiController.setOfficialChannel(g.getTextChannelById(String.valueOf(274732231124320257L)));
            }
        }
        samurai = new SamuraiController(this);
        System.out.println("Ready!" + prefixMap.toString());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() == Bot.self) {
            messagesSent.incrementAndGet();
            return;
        }
        if (event.getAuthor().isBot()) return;

        String token = prefixMap.get(Long.parseLong(event.getGuild().getId()));
        String content = event.getMessage().getRawContent().toLowerCase().trim();

        //if content begins with token ex. "!"
        if (!content.startsWith(token) || content.length() <= token.length() + 3) {
            if (content.equals("<@270044218167132170>"))
                samurai.execute(new Help().setChannelId(Long.valueOf(event.getChannel().getId())));
            return;
        }

        content = content.substring(token.length());
        String key;
        if (!content.contains(" ")) {
            key = content;
            content = null;
        } else {
            key = content.substring(0, content.indexOf(" "));
            content = content.substring(key.length() + 1);
        }
        Action action = samurai.getAction(key);
        if (action == null) return;
        List<String> args = new ArrayList<>();
        if (content != null) {
            String[] argArray = content.substring(content.indexOf(" ") + 1).split("[ ]+");

            for (String argument : argArray) {
                if (!argument.startsWith("<@") && !argument.equals("@everyone") && !argument.equals("@here") && argument.length() != 0)
                    args.add(argument.toLowerCase());
            }
        }
        CombineStrings:
        {
            int i = 0;
            while (i < args.size()) {
                String s = args.get(i);
                if (s.startsWith("\"")) {
                    int j = i + 1;
                    try {
                        while (!args.get(j).endsWith("\""))
                            j++;
                    } catch (IndexOutOfBoundsException e) {
                        break CombineStrings;
                    }
                    StringBuilder p = new StringBuilder();
                    for (int k = i; k <= j; k++) {
                        p.append(args.remove(k));
                    }
                    args.add(p.toString().replace('\"', ' ').trim());
                    i = j + 1;
                }
            }
        }
        action.setArgs(args)
                .setAuthor(event.getMember())
                .setGuildId(Long.valueOf(event.getGuild().getId()))
                .setChannelId(Long.valueOf(event.getChannel().getId()))
                .setMentions(event.getMessage().getMentionedUsers());
        samurai.execute(action);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot() && samurai.isWatching(Long.parseLong(event.getMessageId())))
            samurai.execute(new Reaction()
                    .setChannelId(Long.valueOf(event.getChannel().getId()))
                    .setMessageId(Long.valueOf(event.getMessageId()))
                    .setUser(event.getUser())
                    .setName(event.getReaction().getEmote().getName())
                    .setTime(System.currentTimeMillis()));
    }

    void setJDA(JDA jda) {
        samurai.setJDA(jda);
    }

    public void addPrefix(Long guildId, String prefix) {
        prefixMap.put(guildId, prefix);
    }

    String getPrefix(long guildId) {
        return prefixMap.get(guildId);
    }
}
