package samurai.persistent.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import samurai.persistent.SamuraiMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TonTL on 1/20/2017.
 * Games
 */
public abstract class Game extends SamuraiMessage {

    public static User samurai;

    User A, B;
    User winner;
    User next;

    public Game(User Instigator, User Challenged) {
        A = Instigator;
        B = Challenged;
        winner = null;
    }

    public abstract List<String> getReactions();

    public boolean isPlayer(User player) {
        return player == A || player == B;
    }

    public abstract boolean isNext(User user);

    public abstract void perform(int move, User player);

    public MessageBuilder buildTitle() {
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

    public User getWinner() {
        return winner;
    }

    void setWinner(char w) {
        if (w == 'a') {
            winner = A;
        } else if (w == 'b') {
            winner = B;
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