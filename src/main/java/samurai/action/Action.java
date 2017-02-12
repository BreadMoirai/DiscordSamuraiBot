package samurai.action;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract superclass of all actions
 * Created by TonTL on 2/12/2017.
 */
public abstract class Action implements Callable<Message> {

    protected static final String AVATER_URL = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    protected Member author;
    protected List<User> mentions;
    protected List<String> args;
    protected Long guildId;

    @Override
    public abstract Message call();

    public List<Permission> getPermissions() {
        return Collections.singletonList(Permission.MESSAGE_WRITE);
    }

    public boolean isPersistent() {
        return false;
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
}
