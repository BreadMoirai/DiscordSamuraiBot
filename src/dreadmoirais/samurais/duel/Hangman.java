package dreadmoirais.samurais.duel;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * Hangman
 */
class Hangman extends Game {

    private static final List<String> HANGMAN_REACTIONS = new UnmodifiableArrayList<>(new String[] {""}, 0);
    private static final List<String> HANGMAN_IMAGES = new UnmodifiableArrayList<>(new String[]{"https://puu.sh/twqZM.jpg", "https://puu.sh/twr1V.jpg", "https://puu.sh/twr3V.jpg", "https://puu.sh/twrGc.jpg", "https://puu.sh/twrGx.jpg", "https://puu.sh/twrW2.jpg", "https://puu.sh/twrWn.jpg", "https://puu.sh/twrWJ.jpg", "https://puu.sh/twrWV.jpg", "https://puu.sh/twrX5.jpg", "https://puu.sh/twrXq.jpg"}, 11);

    private String word;

    Hangman(User Instigator, User Challenged) {
        super(Instigator, Challenged);
        next = Challenged;
    }

    @Override
    public List<String> getReactions() {
        return HANGMAN_REACTIONS;
    }

    @Override
    public void perform(int move, User player) {
        if (player==next) {

        }
    }

    @Override
    public Message buildBoard() {
        return null;
    }

    @Override
    public boolean hasEnded() {
        return false;
    }


    boolean setWord(String word) {
        return false;
    }

}

