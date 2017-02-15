package samurai.action;

import net.dv8tion.jda.core.entities.User;

/**
 * @author TonTL
 * @since 4.0
 */
public class Reaction {
    private User user;
    private Long messageId;
    private Long channelId;
    private String emoji;
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

    public Reaction setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Reaction setChannelId(Long channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getEmoji() {
        return emoji;
    }

    public Reaction setEmoji(String emoji) {
        this.emoji = emoji;
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
