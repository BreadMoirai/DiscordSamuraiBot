package samurai.entities.dynamic.duel;

import samurai.entities.base.DynamicMessage;

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


    public Long getA() {
        return A;
    }

    public Long getB() {
        return B;
    }

    public Long getWinner() {
        return winner;
    }

    public Long getNext() {
        return next;
    }

    public String getNameA() {
        return nameA;
    }

    public String getNameB() {
        return nameB;
    }
}