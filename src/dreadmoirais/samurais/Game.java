package dreadmoirais.samurais;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * Created by TonTL on 1/20/2017.
 * Games
 */
public class Game {

    private static final int X_BOUND = 8, Y_BOUND = 6;
    static User samurai;
    static final java.util.List<String> connect4Reactions = new UnmodifiableArrayList<>(new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3", "8\u20e3"}, 8);

    private User A, B;
    private User next;
    private char[][] board;
    private User winner;

    Message message;

    public Game(User Instigator, User Challenged, boolean first) {
        A = Instigator;
        B = Challenged;
        winner = null;
        if (first)
            next = A;
        else
            next = B;

        board = new char[X_BOUND][Y_BOUND];
    }

    boolean isPlayer(User player) {
        return player == A || player == B;
    }

    void dropTile(int columnChoice, User player) {
        if (player == next) {
            for (int y = 0; y < Y_BOUND; y++) {
                if (board[columnChoice][y] == '\u0000') {
                    if (player == A) {
                        board[columnChoice][y] = 'a';
                        next = B;
                    } else {
                        board[columnChoice][y] = 'b';
                        next = A;
                    }
                    return;
                }
            }
        }
    }

    MessageBuilder buildTitle() {
        MessageBuilder mb = new MessageBuilder();
        if (next == A) {
            mb.append("***")
                    .append(A.getAsMention())
                    .append("*** vs. ")
                    .append(B.getAsMention())
                    .append("\n");
        } else {
            mb.append(A.getAsMention())
                    .append(" vs. ***")
                    .append(B.getAsMention())
                    .append("***\n");
        }
        return mb;
    }

    Message buildBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("1\u20e32\u20e33\u20e34\u20e35\u20e36\u20e37\u20e38\u20e3\n");
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

    boolean hasEnded() {
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
                                setWinner(token);
                                return true;
                            }
                        }
                    }

                    //checks diagonal up right
                    if (x < 5 && y < 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y+i] != token) {
                                break;
                            } else if (i == 3) {
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
            if (board[x][Y_BOUND-1]=='\u0000') {
                break;
            } else if (x==7) {
                winner=samurai;
                return true;
            }
        }
        return false;
    }


    private void setWinner(char w) {
        if (w == 'a') {
            winner = A;
        } else if (w == 'b') {
            winner = B;
        }
    }

}