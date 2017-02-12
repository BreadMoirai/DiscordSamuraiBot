package samurai.action;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * Abstract superclass of all actions
 * Created by TonTL on 2/12/2017.
 */
public abstract class Action {

    Member author;
    List<User> mentions;
    List<String> args;

    public abstract List<Permission> getPermissions();

    public abstract boolean isPersistent();

    public Member getAuthor() {
        return author;
    }

    public Action setAuthor(Member author) {
        this.author = author;
        return this;
    }

    public List<User> getMentions() {
        return mentions;
    }

    public Action setMentions(List<User> mentions) {
        this.mentions = mentions;
        return this;
    }

    public List<String> getArgs() {
        return args;
    }

    public Action setArgs(List<String> args) {
        this.args = args;
        return this;
    }
}
