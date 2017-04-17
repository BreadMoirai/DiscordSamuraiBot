package samurai.messages.dynamic.duel.strategy;

import org.jetbrains.annotations.Contract;

/**
 * @author TonTL
 * @version 4/16/2017
 */
public interface ConnectFourStrategy {

    @Contract(pure = true)
    int makeMove(char[][] board);
}
