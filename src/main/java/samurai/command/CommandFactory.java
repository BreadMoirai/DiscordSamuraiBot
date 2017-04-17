package samurai.command;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import org.reflections.Reflections;
import samurai.command.annotations.Key;
import samurai.command.generic.GenericCommand;
import samurai.util.MyLogger;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4.x - 2/21/2017
 */
public class CommandFactory {

    private static final Pattern ARG_PATTERN = Pattern.compile("[\\s+](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    private static final Pattern SAMURAI_MENTION = Pattern.compile("<@([!&])?270044218167132170>( )?");
    private static final Pattern WHITESPACE_MATCHER = Pattern.compile("\\s+");

    private static final HashMap<String, Class<? extends Command>> COMMAND_MAP = new HashMap<>(CommandModule.values().length);

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
                System.out.printf("%-11s mapped to %-10s.%s%n", String.format("\"%s\"", key), name[1], name[2]);
            }
        }
    }

    private static Command newAction(String key) {
        String keyL = key.toLowerCase();
        if (!COMMAND_MAP.containsKey(keyL)) return new GenericCommand();
        try {
            return COMMAND_MAP.get(keyL).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            MyLogger.log("Could not create a new Action", Level.SEVERE, e);
            return null;
        }
    }

    private static Command buildCommand(String prefix, Member author, String content, long channelId, long guildId, long messageId, List<User> mentionedUsers, List<Role> mentionedRoles, List<TextChannel> mentionedChannels, List<Message.Attachment> attachments, TextChannel channel, OffsetDateTime time) {

        //if content does not with prefix ex. "!"
        final Matcher matcher = SAMURAI_MENTION.matcher(content);
        if (matcher.find() && matcher.start() == 0) {
            content = prefix + content.substring(matcher.end());
        }
        if (!content.startsWith(prefix) || content.length() <= prefix.length()) return null;

        content = content.substring(prefix.length());
        String key;
        final Matcher whitespace = WHITESPACE_MATCHER.matcher(content);
        if (!whitespace.find()) {
            key = content;
            content = "";
            if (key.length() > 10) return null;
        } else {
            key = content.substring(0, whitespace.start());
            content = content.substring(whitespace.end()).trim();
        }
        Command command = CommandFactory.newAction(key);
        if (command == null) return null;

        command.setContext(new CommandContext(prefix, key, author, mentionedUsers, mentionedRoles, mentionedChannels, content, attachments, guildId, channelId, messageId, channel, time));

        //System.out.println("New Command: " + command.getClass().getSimpleName());
        return command;
    }

    public static List<String> parseArgs(String content) {
        if (content != null && !content.isEmpty()) {
            return Arrays.stream(ARG_PATTERN.split(content.replace('`', '\"'))).map(String::trim).filter((s) -> !s.isEmpty()).filter(s -> !s.startsWith("<@") && !s.equals("@everyone") && !s.equals("@here")).map(s -> s.replace('\"', ' ')).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    public static Command build(GenericGuildMessageEvent event, String prefix) {
        final Member author = event.getMember();
        final Message message = event.getMessage();
        final long channelId = event.getChannel().getIdLong();
        final long guildId = event.getGuild().getIdLong();
        final List<User> mentionedUsers = message.getMentionedUsers();
        final List<Role> mentionedRoles = event.getMessage().getMentionedRoles();
        final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        final long messageId = message.getIdLong();
        final List<Message.Attachment> attachments = message.getAttachments();
        final String content = message.getRawContent().trim();
        final OffsetDateTime time = message.isEdited() ? message.getEditedTime() : message.getCreationTime();
        return CommandFactory.buildCommand(prefix, author, content, channelId, guildId, messageId, mentionedUsers, mentionedRoles, mentionedChannels, attachments, event.getChannel(), time);
    }

    public static HashMap<String, Class<? extends Command>> getCommandMap() {
        return COMMAND_MAP;
    }
}

