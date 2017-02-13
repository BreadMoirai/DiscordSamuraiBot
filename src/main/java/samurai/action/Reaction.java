package samurai.action;

import net.dv8tion.jda.core.entities.User;

/**
 * @author TonTL
 * @since 4.0
 */
public class Reaction {
    private User user;
    private Long messageId;
    private String emoji;

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

    public String getEmoji() {
        return emoji;
    }

    public Reaction setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }
}
