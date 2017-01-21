package dreadmoirais.samurais;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * Created by TonTL on 1/20/2017.
 */
public class Game {

    private static final int X_BOUND = 8, Y_BOUND = 6;

    private User A, B;
    private User next;
    private char[][] board;

    Message message;

    public Game(User Instigator, User Challenged, boolean first) {
        A = Instigator;
        B = Challenged;
        if (first)
            next = A;
        else
            next = B;

        board = new char[X_BOUND][Y_BOUND];
    }

    public boolean isPlayer(User player) {
        return player==A || player==B;
    }

    public void dropTile(int columnChoice, User player) {
        if (player==next) {
            for (int y = 0; y < Y_BOUND; y++) {
                if (board[columnChoice][y]=='\u0000') {
                    if (player==A) {
                        board[columnChoice][y] = 'a';
                        next = B;
                    }
                    else {
                        board[columnChoice][y] = 'b';
                        next = A;
                    }
                    return;
                }
            }
        }
        return;
    }

    public MessageBuilder buildTitle() {
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

    public Message buildBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("1\u20e32\u20e33\u20e34\u20e35\u20e36\u20e37\u20e38\u20e3\n");
        for (int y = 5; y >= 0; y--) {
            for (int x = 0; x < X_BOUND; x++) {
                if (board[x][y]=='a') {
                    sb.append("\uD83D\uDD34");
                } else if (board[x][y]=='b') {
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
                .setImage(A.getAvatarUrl());
        return buildTitle().setEmbed(eb.build()).build();
    }



}