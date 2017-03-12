package samurai.core.command;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import samurai.core.data.SamuraiGuild;
import samurai.core.entities.base.SamuraiMessage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Superclass of all actions
 *
 * @author TonTL
 * @version 4.0
 */
public abstract class Command implements Callable<Optional<SamuraiMessage>> {

    protected static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    //TheseMembers are neverNull
    protected Member author;
    protected List<Member> mentions;
    protected List<String> args;
    protected List<Message.Attachment> attaches;
    protected long guildId;
    protected long channelId;
    protected long messageId;
    protected SamuraiGuild guild; //@Guild
    protected OffsetDateTime time;

    @Override
    public Optional<SamuraiMessage> call() {
        Optional<SamuraiMessage> messageOptional = Optional.ofNullable(buildMessage());
        messageOptional.ifPresent(samuraiMessage -> samuraiMessage.setChannelId(channelId));
        return messageOptional;
    }

    protected abstract SamuraiMessage buildMessage();

    public Member getAuthor() {
        return author;
    }

    public Command setAuthor(Member author) {
        this.author = author;
        return this;
    }

    public List<Member> getMentions() {
        return mentions;
    }

    public Command setMentions(List<Member> mentions) {
        this.mentions = mentions;
        return this;
    }

    public List<String> getArgs() {
        return args;
    }

    public Command setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public List<Message.Attachment> getAttaches() {
        return attaches;
    }

    public Command setAttaches(List<Message.Attachment> attaches) {
        this.attaches = attaches;
        return this;
    }

    public long getChannelId() {
        return channelId;
    }

    public Command setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    public long getMessageId() {
        return messageId;
    }

    public Command setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public SamuraiGuild getGuild() {
        return guild;
    }

    public Command setGuild(SamuraiGuild guild) {
        this.guild = guild;
        return this;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Command setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public Command setTime(OffsetDateTime time) {
        this.time = time;
        return this;
    }
}
