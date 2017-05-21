package samurai.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.assertj.core.util.Lists;
import samurai.SamuraiDiscord;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SerializableCommandContext implements Serializable {

    private static final long serialVersionUID = 10012L;

    private String prefix;
    private String key;
    private String content;
    private long authorId;
    private long guildId;
    private long channelId;
    private long messageId;
    private long[] mentionedUsers;
    private long[] mentionedRoles;
    private long[] mentionedChannels;
    private OffsetDateTime time;
    private int shardId;

    public SerializableCommandContext() {
    }

    public SerializableCommandContext(CommandContext commandContext) {
        prefix = commandContext.getPrefix();
        key = commandContext.getKey();
        authorId = commandContext.getAuthorId();
        content = commandContext.getContent();
        guildId = commandContext.getGuildId();
        channelId = commandContext.getChannelId();
        messageId = commandContext.getMessageId();
        mentionedUsers = commandContext.getMentionedUsers().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedRoles = commandContext.getMentionedRoles().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedChannels = commandContext.getMentionedChannels().stream().mapToLong(ISnowflake::getIdLong).toArray();
        time = commandContext.getTime();
        shardId = commandContext.getShardId();
    }

    public CommandContext buildContext(SamuraiDiscord samurai) {
        final JDA client = samurai.getMessageManager().getClient();
        final Guild guild = client.getGuildById(guildId);
        if (guild == null) return null;
        final Member author = guild.getMemberById(authorId);
        if (author == null) return null;
        final TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) return null;
        final List<User> userList = Arrays.stream(mentionedUsers).mapToObj(client::getUserById).filter(Objects::nonNull).collect(Collectors.toList());
        final List<TextChannel> channelList = Arrays.stream(mentionedChannels).mapToObj(guild::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());
        final List<Role> roleList = Arrays.stream(mentionedRoles).mapToObj(guild::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
        final CommandContext context = new CommandContext(prefix, key, author, userList, roleList, channelList, content, Lists.emptyList(), guildId, channelId, messageId, channel, time);
        context.setPointTracker(samurai.getPointTracker());
        context.setShardId(shardId);
        return context;
    }
}
