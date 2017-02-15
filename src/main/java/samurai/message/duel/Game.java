package samurai.message.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import samurai.Bot;
import samurai.action.Reaction;
import samurai.message.DynamicMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by TonTL on 1/20/2017.
 * Games
 */
public abstract class Game extends DynamicMessage {

    static final Random random;

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
    public boolean valid(Reaction messageAction) {
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
                winner = Bot.self;
                break;
        }
    }

    public List<User> getLosers() {
        ArrayList<User> losers = new ArrayList<>();
        if (winner == Bot.self) {
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