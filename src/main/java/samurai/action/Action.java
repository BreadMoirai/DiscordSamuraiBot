package samurai.action;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import samurai.message.SamuraiMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Superclass of all actions
 * @author TonTL
 * @version 4.0
 */
public abstract class Action implements Callable<SamuraiMessage> {

    protected static final String AVATER_URL = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    protected Member author;
    protected List<User> mentions;
    protected List<String> args;
    protected Long guildId;
    protected Long channelId;

    @Override
    public SamuraiMessage call() {
        SamuraiMessage message = buildMessage();
        message.setChannelId(channelId);
        return message;
    }

    protected abstract SamuraiMessage buildMessage();

    /**
     * @return a list of Discord Permissions that this action requires
     * @see Permission
     */
    public List<Permission> getPermissions() {
        return Collections.singletonList(Permission.MESSAGE_WRITE);
    }


    public Action setAuthor(Member author) {
        this.author = author;
        return this;
    }

    public Action setMentions(List<User> mentions) {
        this.mentions = mentions;
        return this;
    }

    public Action setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public Action setGuildId(Long guildId) {
        this.guildId = guildId;
        return this;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Action setChannelId(Long channelId) {
        this.channelId = channelId;
        return this;
    }
}
