package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.action.Action;
import samurai.action.ActionFactory;
import samurai.action.admin.Groovy;
import samurai.action.general.Help;
import samurai.message.modifier.Reaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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
    private static final Pattern argPattern = Pattern.compile("[ ](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    private final SamuraiController samurai;
    //private final HashMap<Long, String> prefixMap;


    SamuraiListener() {
        samurai = new SamuraiController();
        //prefixMap = new HashMap<>();
        Groovy.addBinding("samurai", samurai);
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready!");
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT)) return;
        final Member author = event.getMember();
        if (author.getUser().isBot()) return;
        final Message message = event.getMessage();
        final long channelId = Long.parseLong(event.getChannel().getId());
        final long guildId = Long.parseLong(event.getGuild().getId());
        process(author, message, channelId, guildId);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Member author = event.getGuild().getMember(event.getAuthor());
        if (author.getUser().getId().equals(Bot.BOT_ID)) {
            messagesSent.incrementAndGet();
            return;
        } else if (author.getUser().isBot()) return;
        final Message message = event.getMessage();
        final long channelId = Long.parseLong(event.getChannel().getId());
        final long guildId = Long.parseLong(event.getGuild().getId());
        process(author, message, channelId, guildId);
    }

    private void process(Member author, Message message, long channelId, long guildId) {
        final String token;
//        if (prefixMap.containsKey(guildId))
//            token = prefixMap.get(guildId);
//        else {
            token = samurai.getPrefix(guildId);
//            prefixMap.put(guildId, token);
//        }

        String content = message.getRawContent().trim();

        //if content begins with token ex. "!"

        if (!content.startsWith(token) || content.length() <= token.length() + 3) {
            if (content.equals("<@270044218167132170>"))
                samurai.execute(new Help()
                        .setChannelId(channelId)
                        .setGuildId(guildId)
                        .setArgs(new ArrayList<>()));
            return;
        }

        content = content.substring(token.length());
        String key;
        if (!content.contains(" ")) {
            key = content;
            content = null;
            if (key.length() > 10) return;
        } else {
            key = content.substring(0, content.indexOf(' '));
            content = content.substring(content.indexOf(' ')).trim();
        }
        Action action = ActionFactory.newAction(key);
        if (action == null) return;
        List<String> args = new ArrayList<>();

        if (content != null && !content.equals("")) {
            String[] argArray = argPattern.split(content.replace('`', '\"'));
            for (String argument : argArray)
                if (!argument.startsWith("<@") && !argument.equals("@everyone") && !argument.equals("@here") && argument.length() != 0)
                    args.add(argument.replace("\"", "").trim());
        }


        action.setArgs(args)
                .setAuthor(author)
                .setGuildId(guildId)
                .setChannelId(channelId)
                .setMessageId(Long.parseLong(message.getId()))
                .setMentions(message.getMentionedUsers())
                .setAttaches(message.getAttachments());
        samurai.execute(action);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot() && samurai.isWatching(Long.parseLong(event.getMessageId())))
            samurai.execute(new Reaction()
                    .setChannelId(Long.parseLong(event.getChannel().getId()))
                    .setMessageId(Long.parseLong(event.getMessageId()))
                    .setUser(event.getUser())
                    .setName(event.getReaction().getEmote().getName())
                    .setTime(System.currentTimeMillis()));
    }

    void setJDA(JDA jda) {
        samurai.setJDA(jda);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        System.out.println("Shutting Down");
        try {
            Runtime.getRuntime().exec("cmd /c start xcopy C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\build\\resources\\main\\samurai\\data C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data /d /e /f /h /i /s /y /z /exclude:C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data\\exclude.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        samurai.shutdown();
    }


}
