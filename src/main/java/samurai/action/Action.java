package samurai.action;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import samurai.action.generic.DuelAction;
import samurai.action.generic.GuildAction;
import samurai.action.generic.HelpAction;
import samurai.action.generic.InviteAction;
import samurai.persistent.SamuraiMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract superclass of all actions
 * Created by TonTL on 2/12/2017.
 */
public abstract class Action implements Callable<SamuraiMessage> {

    protected static final String AVATER_URL = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    protected Member author;
    protected List<User> mentions;
    protected List<String> args;
    protected Long guildId;
    protected MessageChannel channel;

    /**
     * @param key a string corresponding to the action. ex. "help" or "setprefix"
     * @return A Action if key is valid, null otherwise.
     */
    public static Action getAction(String key) {
        switch (key) {
            case "help":
                return new HelpAction();
            case "guild":
                return new GuildAction();
            case "invite":
                return new InviteAction();
            case "duel":
                return new DuelAction();
            default:
                return null;
        }
    }

    @Override
    public abstract SamuraiMessage call();

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

    public Action setChannel(MessageChannel channel) {
        this.channel = channel;
        return this;
    }
}
