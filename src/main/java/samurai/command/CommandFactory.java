package samurai.command;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import org.reflections.Reflections;
import samurai.command.annotations.Key;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4.x - 2/21/2017
 */
public class CommandFactory {

    private static final Pattern argPattern = Pattern.compile("[ ](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    private static final HashMap<String, Class<? extends Command>> COMMAND_MAP = new HashMap<>(Commands.values().length);
    private static final Pattern SAMURAI_MENTION = Pattern.compile("<@(!)?270044218167132170>( )?");

    private CommandFactory() {
    }

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
                COMMAND_MAP.put(key, action);
                System.out.printf("%-11s mapped to %-7s.%s%n", String.format("\"%s\"", key), name[1], name[2]);
            }
        }
    }

    private static Command newAction(String key) {
        if (!COMMAND_MAP.containsKey(key)) return new GenericCommand();
        try {
            return COMMAND_MAP.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //todo
            return null;
        }
    }

    private static Command buildCommand(String token, Member author, String content, long channelId, long guildId, long messageId, List<Member> mentionedMembers, List<Role> mentionedRoles, List<TextChannel> mentionedChannels, List<Message.Attachment> attachments, TextChannel channel, OffsetDateTime time) {

        //if content does not with token ex. "!"
        final Matcher matcher = SAMURAI_MENTION.matcher(content);
        if (matcher.find() && matcher.start() == 0) {
            matcher.replaceFirst(token);
        }
        if (!content.startsWith(token) || content.length() <= token.length()) return null;

        content = content.substring(token.length());
        String key;
        if (!content.contains(" ")) {
            key = content;
            content = "";
            if (key.length() > 10) return null;
        } else {
            key = content.substring(0, content.indexOf(' '));
            content = content.substring(content.indexOf(' ')).trim();
        }
        Command command = CommandFactory.newAction(key);
        if (command == null) return null;

        command.setContext(new CommandContext(key, author, mentionedMembers, mentionedRoles, mentionedChannels, content, attachments, guildId, channelId, messageId, channel, time));

        //System.out.println("New Command: " + command.getClass().getSimpleName());
        return command;
    }

    public static List<String> parseArgs(String content) {
        if (content != null && !content.isEmpty()) {
            return Arrays.stream(argPattern.split(content.replace('`', '\"'))).map(String::trim).filter((s) -> !s.isEmpty()).filter(s -> !s.startsWith("<@") && !s.equals("everyone") && !s.equals("@here")).map(s -> s.replace('\"', ' ')).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    public static Command build(GenericGuildMessageEvent event, String prefix) {
        final Member author = event.getMember();
        final Message message = event.getMessage();
        final long channelId = Long.parseLong(event.getChannel().getId());
        final long guildId = Long.parseLong(event.getGuild().getId());
        final List<Member> mentionedMembers;
        {
            final Guild g = message.getGuild();
            final List<User> mentionedUsers = message.getMentionedUsers();
            final List<Member> members = new ArrayList<>(mentionedUsers.size());
            mentionedUsers.forEach(user -> members.add(g.getMember(user)));
            mentionedMembers = Collections.unmodifiableList(members);
        }
        final List<Role> mentionedRoles = event.getMessage().getMentionedRoles();
        final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        final long messageId = Long.parseLong(message.getId());
        final List<Message.Attachment> attachments = message.getAttachments();
        final String content = message.getRawContent().trim();
        final OffsetDateTime time = message.isEdited() ? message.getEditedTime() : message.getCreationTime();
        return CommandFactory.buildCommand(prefix, author, content, channelId, guildId, messageId, mentionedMembers, mentionedRoles, mentionedChannels, attachments, event.getChannel(), time);
    }

    public static HashMap<String, Class<? extends Command>> getCommandMap() {
        return COMMAND_MAP;
    }
}

