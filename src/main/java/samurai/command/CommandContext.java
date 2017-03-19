package samurai.command;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.entities.SamuraiGuild;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class CommandContext {
    private final String key;
    private final Member author;
    private final List<Member> mentions;
    private final String content;
    private final List<Message.Attachment> attaches;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private List<String> args;
    private SamuraiGuild guild;
    private TextChannel channel;
    private OffsetDateTime time;
    private int shardId;

    public CommandContext(String key, Member author, List<Member> mentions, String content, List<Message.Attachment> attaches, long guildId, long channelId, long messageId, TextChannel channel, OffsetDateTime time) {
        this.key = key;
        this.author = author;
        this.mentions = mentions;
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

    public List<Member> getMentions() {
        return mentions;
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

    public SamuraiGuild getGuild() {
        return guild;
    }

    public CommandContext setGuild(SamuraiGuild guild) {
        this.guild = guild;
        return this;
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
}
