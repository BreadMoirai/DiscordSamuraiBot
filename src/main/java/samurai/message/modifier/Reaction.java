package samurai.message.modifier;

import net.dv8tion.jda.core.entities.User;

/**
 * @author TonTL
 * @since 4.0
 */
public class Reaction {
    private User user;
    private long messageId;
    private long channelId;
    private String name;
    private long time;

    public User getUser() {
        return user;
    }

    public Reaction setUser(User user) {
        this.user = user;
        return this;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Reaction setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Reaction setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Reaction setName(String name) {
        this.name = name;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Reaction setTime(long time) {
        this.time = time;
        return this;
    }

}
