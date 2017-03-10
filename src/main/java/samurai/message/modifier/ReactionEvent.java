package samurai.message.modifier;

/**
 * @author TonTL
 * @since 4.0
 */
public class ReactionEvent {
    private long user;
    private long messageId;
    private long channelId;
    private String name;
    private long time;

    public long getUser() {
        return user;
    }

    public ReactionEvent setUser(long user) {
        this.user = user;
        return this;
    }

    public Long getMessageId() {
        return messageId;
    }

    public ReactionEvent setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Long getChannelId() {
        return channelId;
    }

    public ReactionEvent setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ReactionEvent setName(String name) {
        this.name = name;
        return this;
    }

    public long getTime() {
        return time;
    }

    public ReactionEvent setTime(long time) {
        this.time = time;
        return this;
    }

}
