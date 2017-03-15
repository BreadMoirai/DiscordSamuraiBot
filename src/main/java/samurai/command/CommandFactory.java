package samurai.command;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import org.reflections.Reflections;
import samurai.command.annotations.Key;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4.x - 2/21/2017
 */
public class CommandFactory {

    private static final Pattern argPattern = Pattern.compile("[ ](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    private static final HashMap<String, Class<? extends Command>> actionMap = new HashMap<>();

    public static void initialize() {
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
                System.out.printf("%-11s mapped to %-7s.%s%n", String.format("\"%s\"", key), name[1], name[2]);
            }
        }
    }

    private CommandFactory() {
    }

    private static Command newAction(String key) {
        if (!actionMap.containsKey(key)) return new GenericCommand();
        try {
            return actionMap.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //todo
            return null;
        }
    }

    public static Set<String> keySet() {
        return actionMap.keySet();
    }

    private static Command buildCommand(String token, Member author, String content, long channelId, long guildId, long messageId, List<Member> mentions, List<Message.Attachment> attachments, TextChannel channel, OffsetDateTime time) {

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

        command.setContext(new CommandContext(key, author, mentions, content, attachments, guildId, channelId, messageId, channel, time));

        //System.out.println("New Command: " + command.getClass().getSimpleName());
        return command;
    }

    static List<String> parseArgs(String content) {
        if (content != null && !content.equals("")) {
            return Arrays.stream(argPattern.split(content.replace('`', '\"'))).map(String::trim).filter((s) -> !s.isEmpty()).filter(s -> !s.startsWith("<@") && !s.equals("everyone") && !s.equals("@here")).map(s -> s.replace("\"", "")).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        } else return Collections.emptyList();
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
        final OffsetDateTime time = message.isEdited() ? message.getEditedTime() : message.getCreationTime();
        return CommandFactory.buildCommand(prefix, author, content, channelId, guildId, messageId, mentions, attachments, event.getChannel(), time);
    }
}

