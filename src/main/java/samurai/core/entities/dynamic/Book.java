package samurai.core.entities.dynamic;

import samurai.core.entities.base.DynamicMessage;
import samurai.core.events.ReactionEvent;
import samurai.core.events.listeners.ReactionListener;

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

    public Book(int page, ArrayList<String> book) {
        this.page = page;
        this.book = book;
    }

    public Book(ArrayList<String> book) {
        this(0, book);
    }

    @Override
    protected void onReady() {
        if (book.size() == 1) {
            submitNewMessage(book.get(1));
            unregister();
        } else
            submitNewMessage("```md < Retrieving User List > ```", newMenu(REACTIONS).andThen(message -> message.editMessage(book.get(page))));
    }

    @Override
    public void onReaction(ReactionEvent event) {
        final int i = REACTIONS.indexOf(event.getName());
        if (i == -1) return;
        if (i == 1) {
            clearReactions();
            unregister();
        } else {
            if (i == 0) if (page != 0) page--;
            else return;
            else if (i == 2) if (page != book.size() - 1) page++;
            else return;
            else return;
            updateMessage(book.get(page));
            removeReaction(event.getChannelId(), event.getUserId(), event.getName());
        }
    }
}

