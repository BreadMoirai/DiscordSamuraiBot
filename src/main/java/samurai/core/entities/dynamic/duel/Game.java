package samurai.core.entities.dynamic.duel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import samurai.core.Bot;
import samurai.core.entities.DynamicMessage;
import samurai.core.events.ReactionEvent;

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

    Long A, B, winner, next;
    String nameA, nameB;

    Game(User instigator, User... challengers) {
        super();
        A = Long.valueOf(instigator.getId());
        nameA = instigator.getName();
        if (challengers.length > 0) {
            B = Long.valueOf(challengers[0].getId());
            nameB = challengers[0].getName();
        }
        else B = null;
        winner = null;
    }

    @Override
    public boolean valid(ReactionEvent action) {
        return false;
    }

    MessageBuilder buildTitle() {
        MessageBuilder mb = new MessageBuilder();
        if (next.equals(A)) {
            mb.append("<@").append(A)
                    .append("> \uD83C\uDD9A ")
                    .append(nameB)
                    .append("\n");
        } else {
            mb.append(nameA)
                    .append(" \uD83C\uDD9A <@")
                    .append(B)
                    .append(">\n");
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
                winner = Long.valueOf(Bot.ID);
                break;
        }
    }

    public List<Long> getLosers() {
        ArrayList<Long> losers = new ArrayList<>();
        if (winner.equals(Long.parseLong(Bot.ID))) {
            losers.add(A);
            losers.add(B);
        } else if (winner.equals(A)) {
            losers.add(B);
        } else {
            losers.add(A);
        }
        return losers;
    }

}