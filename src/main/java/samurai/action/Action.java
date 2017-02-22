package samurai.action;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import samurai.SamuraiController;
import samurai.SamuraiListener;
import samurai.data.SamuraiGuild;
import samurai.message.SamuraiMessage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Superclass of all actions
 * @author TonTL
 * @version 4.0
 */
public abstract class Action implements Callable<Optional<SamuraiMessage>> {

    protected static final String AVATER_URL = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";

    //TheseMembers are neverNull
    protected Member author;
    protected List<User> mentions;
    protected List<String> args;
    protected List<Message.Attachment> attaches;
    protected Long guildId;
    protected Long channelId;
    protected Long messageId;

    //optional members
    protected JDA client; //@Client
    protected SamuraiGuild guild; //@Guild
    protected SamuraiListener listener; //@Listener
    protected Set<String> actionKeySet;
    protected SamuraiController controller;

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

    public Long getGuildId() {
        return guildId;
    }

    public Action setGuildId(Long guildId) {
        this.guildId = guildId;
        return this;
    }

    public Action setChannelId(Long channelId) {
        this.channelId = channelId;
        return this;
    }

    public Action setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Action setAttaches(List<Message.Attachment> attaches) {
        this.attaches = attaches;
        return this;
    }

    public void setClient(JDA client) {
        this.client = client;
    }

    public Action setGuild(SamuraiGuild guild) {
        this.guild = guild;
        return this;
    }

    public void setListener(SamuraiListener listener) {
        this.listener = listener;
    }

    public void setKeySet(Set<String> actionKeySet) {
        this.actionKeySet = actionKeySet;
    }

    public void setController(SamuraiController controller) {
        this.controller = controller;
    }
}
