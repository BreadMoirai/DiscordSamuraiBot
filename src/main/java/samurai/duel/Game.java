package samurai.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * Created by TonTL on 1/20/2017.
 * Games
 */
public abstract class Game {

    public static User samurai;

    public Message message;

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

    public boolean isNext(User user) {
        return user == next;
    }

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

//    void setWinner(char w) {
//        if (w == 'a') {
//            winner = A;
//            SamuraiFile.incrementStat("Duels Won");
//            userDataA.incrementStat("Duels Fought");
//            userDataB.incrementStat("Duels Fought");
//        } else if (w == 'b') {
//            winner = B;
//            userDataB.incrementStat("Duels Won");
//            userDataB.incrementStat("Duels Fought");
//            userDataA.incrementStat("Duels Fought");
//        }
//    }
//
//    public void setData(HashMap<String, BotData.UserData> users) {
//        userDataA = users.get(A.getId());
//        userDataB = users.get(B.getId());
//    }
}