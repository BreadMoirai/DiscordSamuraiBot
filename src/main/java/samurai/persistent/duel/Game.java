package samurai.persistent.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import samurai.SamuraiListener;
import samurai.action.Reaction;
import samurai.persistent.SamuraiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by TonTL on 1/20/2017.
 * Games
 */
public abstract class Game extends SamuraiMessage {

    public static final Random random;
    public static User samurai;

    static {
        random = new Random();
    }

    User A, B;
    User winner;
    User next;

    Game(User Instigator, User... Challenged) {
        A = Instigator;
        if (Challenged.length > 0)
            B = Challenged[0];
        else B = null;
        winner = null;
    }

    @Override
    public boolean valid(Reaction reaction) {
        return false;
    }

    MessageBuilder buildTitle() {
        MessageBuilder mb = new MessageBuilder();
        if (next == A) {
            mb.append("***")
                    .append(A.getAsMention())
                    .append("*** \uD83C\uDD9A ")
                    .append(B.getAsMention())
                    .append("\n");
        } else {
            mb.append(A.getAsMention())
                    .append(" \uD83C\uDD9A ***")
                    .append(B.getAsMention())
                    .append("***\n");
        }
        return mb;
    }

    public abstract Message buildBoard();

    public abstract boolean hasEnded();

    void setWinner(char w) {
        switch (w) {
            case 'a':
                winner = A;
                break;
            case 'b':
                winner = B;
                break;
            default:
                winner = SamuraiListener.getSelf();
                break;
        }
    }

    public List<User> getLosers() {
        ArrayList<User> losers = new ArrayList<>();
        if (winner == samurai) {
            losers.add(A);
            losers.add(B);
        } else if (winner == A) {
            losers.add(B);
        } else {
            losers.add(A);
        }
        return losers;
    }

}