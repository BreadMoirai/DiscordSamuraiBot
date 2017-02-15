package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.action.Action;
import samurai.action.Reaction;
import samurai.action.generic.HelpAction;
import samurai.data.SamuraiFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener for SamuraiBot
 * This class listens to events from discord, takes the required information by building the appropriate action and passing it to SamuraiController#execute
 *
 * @author TonTL
 * @version 4.0
 * @see SamuraiController
 * @since 2/12/2017.
 */
@SuppressWarnings("Duplicates")
public class SamuraiListener extends ListenerAdapter {
    private static final AtomicInteger messagesSent = new AtomicInteger(0);
    private final HashMap<Long, String> prefix;
    private SamuraiController samurai;


    SamuraiListener() {
        prefix = new HashMap<>();
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {
            long guildId = Long.parseLong(g.getId());
            if (!SamuraiFile.hasFile(guildId)) {
                SamuraiFile.writeGuildData(g);
                prefix.put(guildId, "!");
            } else {
                // wait execute
                prefix.put(guildId, SamuraiFile.getPrefix(guildId));
            }

            if (guildId == 233097800722808832L) {
                SamuraiController.setOfficialChannel(g.getTextChannelById(String.valueOf(274732231124320257L)));
            }
        }
        samurai = new SamuraiController();
        System.out.println("Ready!" + prefix.toString());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() == Bot.self) {
            messagesSent.incrementAndGet();
            return;
        }
        if (event.getAuthor().isBot()) return;

        String token = prefix.get(Long.parseLong(event.getGuild().getId()));
        String content = event.getMessage().getRawContent().toLowerCase().trim();

        //if content begins with token ex. "!"
        if (!content.startsWith(token) || content.length() <= token.length() + 3) {
            if (content.equals("<@270044218167132170>"))
                samurai.execute(new HelpAction().setChannelId(Long.valueOf(event.getChannel().getId())));
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
        Action action = Action.getAction(key);
        if (action == null) return;

        if (content != null) {
            String[] argArray = content.substring(content.indexOf(" ") + 1).split("[ ]+");
            List<String> args = new ArrayList<>();
            for (String argument : argArray) {
                if (!argument.startsWith("<@") && !argument.equals("@everyone") && !argument.equals("@here") && argument.length() != 0)
                    args.add(argument);
            }
            action.setArgs(args);
        }

        {
        }
        action.setAuthor(event.getMember())
                .setGuildId(Long.valueOf(event.getGuild().getId()))
                .setChannelId(Long.valueOf(event.getChannel().getId()))
                .setMessageId(Long.valueOf(event.getMessage().getId()))
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
                    .setEmoji(event.getReaction().getEmote().getName())
                    .setTime(System.currentTimeMillis()));
    }

    void setJDA(JDA jda) {
        samurai.setJDA(jda);
    }
}
