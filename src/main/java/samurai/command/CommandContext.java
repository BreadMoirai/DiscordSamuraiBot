package samurai.command;

import net.dv8tion.jda.core.entities.*;
import samurai.Bot;
import samurai.database.Database;
import samurai.entities.model.SGuild;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class CommandContext {
    private final String prefix;
    private final String key;
    private final Member author;
    private final List<User> mentionedUsers;
    private List<Member> mentionedMembers;
    private final List<Role> mentionedRoles;
    private final String content;
    private final List<Message.Attachment> attaches;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private List<TextChannel> mentionedChannels;
    private List<String> args;
    private SGuild sGuild;
    private final TextChannel channel;
    private final OffsetDateTime time;
    private int shardId;

    public CommandContext(String prefix, String key, Member author, List<User> mentionedUsers, List<Role> mentionedRoles, List<TextChannel> mentionedChannels, String content, List<Message.Attachment> attaches, long guildId, long channelId, long messageId, TextChannel channel, OffsetDateTime time) {
        this.prefix = prefix;
        this.key = key;
        this.author = author;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
        this.mentionedChannels = mentionedChannels;
        this.content = content;
        this.attaches = attaches;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.channel = channel;
        this.time = time;
    }

    public long getGuildId() {
        return guildId;
    }

    public Member getAuthor() {
        return author;
    }

    public List<User> getMentionedUsers() {
        return mentionedUsers;
    }

    public List<Member> getMentionedMembers() {
        if (mentionedMembers == null) {
            final Guild guild = channel.getGuild();
            mentionedMembers = Collections.unmodifiableList(mentionedUsers.stream().map(guild::getMember).collect(Collectors.toList()));
        }
        return mentionedMembers;
    }

    public String getContent() {return content; }

    public List<String> getArgs() {
        if (args == null) return (args = CommandFactory.parseArgs(content));
        else return args;
    }

    public List<Message.Attachment> getAttaches() {
        return attaches;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public SGuild getsGuild() {
        if (sGuild == null) {
            final long[] userID = channel.getGuild().getMembers().stream().map(Member::getUser).mapToLong(User::getIdLong).toArray();
            final Optional<SGuild> guildOptional = Database.getDatabase().getGuild(guildId, userID);
            if (guildOptional.isPresent())
                this.sGuild = guildOptional.get();
            else {
                final Optional<SGuild> guild = Database.getDatabase().createGuild(guildId, CommandModule.getDefaultEnabledCommands());
                guild.ifPresent(sGuild -> {
                    sGuild.getManager().setUsers(userID);
                    this.sGuild = sGuild;
                });
            }
        }
        return sGuild;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public String getKey() {
        return key;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public boolean hasContent() {
        return content.length() > 0;
    }

    public boolean isSource() {
        return guildId == Bot.SOURCE_GUILD;
    }

    public List<TextChannel> getMentionedChannels() {
        return mentionedChannels;
    }

    public List<Role> getMentionedRoles() {
        return mentionedRoles;
    }

    public Guild getDiscordGuild() {
        return channel.getGuild();
    }

    public long getAuthorId() {
        return author.getUser().getIdLong();
    }

    public String getPrefix() {
        return prefix;
    }
}
