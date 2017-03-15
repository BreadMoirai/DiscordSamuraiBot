
package samurai.entities.dynamic.duel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import samurai.entities.base.DynamicMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@SuppressFBWarnings
class Hangman extends DynamicMessage {

    private static final List<String> HANGMAN_REACTIONS = Collections.unmodifiableList(Arrays.asList());
    private static final List<String> HANGMAN_IMAGES = Collections.unmodifiableList(Arrays.asList("https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/twrWJ.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"));

    private User a;
    private String word;

    public Hangman(User a, String word) {
        this.a = a;
        this.word = word;
    }

    @Override
    protected void onReady(TextChannel channel) {

    }
}