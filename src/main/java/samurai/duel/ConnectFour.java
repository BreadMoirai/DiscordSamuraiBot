package samurai.duel;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * Connct Four
 */
public class ConnectFour extends Game {


    public static final List<String> CONNECTFOUR_REACTIONS = new UnmodifiableArrayList<>(new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"}, 7);

    private static final int X_BOUND = 7, Y_BOUND = 6;

    private char[][] board;

    public ConnectFour(User Instigator, User Challenged, boolean first) {
        super(Instigator, Challenged);
        if (first)
            next = A;
        else
            next = B;
        board = new char[X_BOUND][Y_BOUND];
    }

    @Override
    public List<String> getReactions() {
        return CONNECTFOUR_REACTIONS;
    }

    @Override
    public boolean isNext(User user) {
        return user == next;
    }

    @Override
    public void perform(int move, User player) {
        if (player == next) {
            for (int y = 0; y < Y_BOUND; y++) {
                if (board[move][y] == '\u0000') {
                    if (player == A) {
                        board[move][y] = 'a';
                        next = B;
                    } else {
                        board[move][y] = 'b';
                        next = A;
                    }
                    return;
                }
            }
        }
    }

    @Override
    public Message buildBoard() {
        StringBuilder sb = new StringBuilder();
        for (String emojiNum : CONNECTFOUR_REACTIONS)
            sb.append(emojiNum);
        sb.append("\n");
        for (int y = 5; y >= 0; y--) {
            for (int x = 0; x < X_BOUND; x++) {
                if (board[x][y] == 'a') {
                    sb.append("\uD83D\uDD34");
                } else if (board[x][y] == 'b') {
                    sb.append("\uD83D\uDD35");
                } else {
                    sb.append("\u26aa");
                }
            }
            sb.append("\n");
        }
        EmbedBuilder eb = new EmbedBuilder()
                .addField("Connect 4", sb.toString(), true)
                .setColor(Color.BLACK)
                .setFooter("SamuraiGames\u2122", "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg");
        if (winner != null) {
            eb.setImage(winner.getAvatarUrl()).addField("The Winner is:", winner.getName(), false);
        }
        return buildTitle().setEmbed(eb.build()).build();
    }

    @Override
    public boolean hasEnded() {
        for (int x = 0; x < X_BOUND; x++) {
            for (int y = 0; y < Y_BOUND; y++) {

                char token = board[x][y];
                if (token != '\u0000') {

                    //checks horizontal right
                    if (x < 5) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y] != token) {
                                break;
                            } else if (i == 3) {
                                //todo
                                setWinner(token);
                                return true;
                            }
                        }
                    }

                    //checks diagonal up right
                    if (x < 5 && y < 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                // todo
                                setWinner(token);
                                return true;
                            }
                        }
                    }

                    //checks vertical up
                    if (y < 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                // todo
                                setWinner(token);
                                return true;
                            }
                        }
                    }

                    //checks diagonal up left
                    if (x > 2 && y < 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x - i][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                // todo
                                setWinner(token);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        //check draw
        for (int x = 0; x < X_BOUND; x++) {
            if (board[x][Y_BOUND - 1] == '\u0000') {
                break;
            } else if (x == 7) {
                winner = samurai;
                return true;
            }
        }
        return false;
    }

}