package samurai.entities.dynamic;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.entities.base.DynamicMessage;
import samurai.events.ReactionEvent;
import samurai.events.listeners.ReactionListener;
import samurai.util.wrappers.MessageWrapper;
import samurai.util.wrappers.SamuraiWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
public class Book extends DynamicMessage implements ReactionListener {

    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("⬅", "\uD83D\uDD12", "➡"));
    private static final int PAGE_SIZE = 10;
    ArrayList<String> book;
    private int page;
    private MessageWrapper message;

    public Book(int page, ArrayList<String> book) {
        this.page = page;
        this.book = book;
    }

    public Book(ArrayList<String> book) {
        this(0, book);
    }

    @Override
    protected void onReady(TextChannel channel) {
        if (book.size() == 1) {
            channel.sendMessage(book.get(0));
            unregister();
        } else
        channel.sendMessage("```md < Retrieving User List > ```").queue(message1 -> message = SamuraiWrapper.wrap(message1));
    }

    @Override
    public void onReaction(ReactionEvent event) {
        final int i = REACTIONS.indexOf(event.getName());
        if (i == -1) return;
        if (i == 1) {
            message.clearReactions();
            unregister();
        } else {
            if (i == 0) if (page != 0) page--;
            else return;
            else if (i == 2) if (page != book.size() - 1) page++;
            else return;
            else return;
            message.editMessage(book.get(page));
            message.removeReaction(event);
        }
    }
}

