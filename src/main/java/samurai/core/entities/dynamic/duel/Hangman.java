
package samurai.core.entities.dynamic.duel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import samurai.core.events.ReactionEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
@SuppressFBWarnings
class Hangman extends Game {

    private static final List<String> HANGMAN_REACTIONS = Collections.unmodifiableList(Arrays.asList());
    private static final List<String> HANGMAN_IMAGES = Collections.unmodifiableList(Arrays.asList("https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/twrWJ.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"));

    private String word;

    public Hangman(User a, String word) {
        super(a);
        this.word = word;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    protected void execute(ReactionEvent action) {

    }

    @Override
    public Consumer<Message> createConsumer() {
        return null;
    }

    @Override
    protected int getLastStage() {
        return 0;
    }

    @Override
    public boolean hasEnded() {
        return false;
    }
}