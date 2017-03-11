package samurai.core.command;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import org.reflections.Reflections;
import samurai.core.Bot;
import samurai.core.command.annotations.Key;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TonTL
 * @version 4.x - 2/21/2017
 */
public class CommandFactory {

    private static final Pattern argPattern;

    private static final HashMap<String, Class<? extends Command>> actionMap;

    static {
        argPattern = Pattern.compile("[ ](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        actionMap = new HashMap<>();
        Reflections reflections = new Reflections("samurai.command");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> action : classes) {
            Key actionKey = action.getAnnotation(Key.class);
            if (actionKey == null || actionKey.value().length == 0) {
                System.err.printf("No key found for %s%n", action.getName());
                continue;
            }
            String[] name = action.getName().substring(15).split("\\.");
            for (String key : actionKey.value()) {
                actionMap.put(key, action);
                System.out.printf("%-10s mapped to %-7s.%s%n", String.format("\"%s\"", key), name[0], name[1]);
            }
        }
    }

    private CommandFactory() {
    }

    private static Command newAction(String key) {
        if (!actionMap.containsKey(key)) return new GenericCommand().setKey(key);
        try {
            return actionMap.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Bot.logError(e);
            return null;
        }
    }

    public static Set<String> keySet() {
        return actionMap.keySet();
    }

    private static Command buildCommand(String token, Member author, String content, long channelId, long guildId, long messageId, List<Member> mentions, List<Message.Attachment> attachments) {

        //if content does not with token ex. "!"
        if (content.startsWith("<@270044218167132170>"))
            content = content.replaceFirst("<@270044218167132170>( )?", token);
        if (!content.startsWith(token) || content.length() <= token.length() + 3) return null;

        content = content.substring(token.length());
        String key;
        if (!content.contains(" ")) {
            key = content;
            content = null;
            if (key.length() > 10) return null;
        } else {
            key = content.substring(0, content.indexOf(' '));
            content = content.substring(content.indexOf(' ')).trim();
        }
        Command command = CommandFactory.newAction(key);
        if (command == null) return null;

        List<String> args = parseArgs(content);

        command.setArgs(args)
                .setAuthor(author)
                .setGuildId(guildId)
                .setChannelId(channelId)
                .setMessageId(messageId)
                .setMentions(mentions)
                .setAttaches(attachments);
        return command;
    }

    private static List<String> parseArgs(String content) {
        List<String> args = new ArrayList<>();
        if (content != null && !content.equals("")) {
            String[] argArray = argPattern.split(content.replace('`', '\"'));
            for (String argument : argArray)
                if (!argument.startsWith("<@") && !argument.equals("@everyone") && !argument.equals("@here") && argument.length() != 0)
                    args.add(argument.replace("\"", "").trim());
        }
        return args;
    }

    public static Command build(GenericGuildMessageEvent event, String prefix) {
        final Member author = event.getMember();
        final Message message = event.getMessage();
        final long channelId = Long.parseLong(event.getChannel().getId());
        final long guildId = Long.parseLong(event.getGuild().getId());
        final List<Member> mentions;
        {
            final Guild g = message.getGuild();
            final List<Member> members = new ArrayList<>();
            final List<User> mentionedUsers = message.getMentionedUsers();
            mentionedUsers.forEach(user -> members.add(g.getMember(user)));
            mentions = Collections.unmodifiableList(members);
        }
        final long messageId = Long.parseLong(message.getId());
        final List<Message.Attachment> attachments = message.getAttachments();
        final String content = message.getRawContent().trim();
        return CommandFactory.buildCommand(prefix, author, content, channelId, guildId, messageId, mentions, attachments);
    }
}

