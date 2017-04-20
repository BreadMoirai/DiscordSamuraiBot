package samurai.messages.impl.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.listeners.ReactionListener;

import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4/18/2017
 */
public class Prompt extends DynamicMessage implements ReactionListener, UniqueMessage {

    private static final String YES = "✅", NO = "❎";

    private final Message prompt;
    private final Consumer<Message> onYes, onNo;
    private Message message;

    public Prompt(Message prompt, Consumer<Message> onYes, Consumer<Message> onNo) {
        this.prompt = prompt;
        if (onYes == null) {
            this.onYes = message -> {
            };
        } else
            this.onYes = onYes;
        if (onNo == null) {
            this.onNo = message -> {
            };
        } else
            this.onNo = onNo;
    }


    @Override
    protected Message initialize() {
        return prompt;
    }

    @Override
    protected void onReady(Message message) {
        this.message = message;
        message.addReaction(YES).queue();
        message.addReaction(NO).queue();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final long userId = event.getUser().getIdLong();
        if (userId != 0 && userId != this.getAuthorId()) return;
        final String name = event.getReaction().getEmote().getName();
        switch (name) {
            case YES:
                onYes.accept(message);
                unregister();
                break;
            case NO:
                onNo.accept(message);
                unregister();
                break;
            default:
        }

    }

    @Override
    public boolean shouldPrompt() {
        return false;
    }

    @Override
    public void close(TextChannel channel) {
        channel.deleteMessageById(getMessageId()).queue();
    }
}
