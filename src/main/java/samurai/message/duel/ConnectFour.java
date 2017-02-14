package samurai.message.duel;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import samurai.action.Reaction;

import java.awt.*;
import java.util.List;


/**
 * ConnectFour game
 * @since 4.0
 */
public class ConnectFour extends Game {


    public static final List<String> CONNECTFOUR_REACTIONS = new UnmodifiableArrayList<>(new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"}, 7);

    private static final int X_BOUND = 7, Y_BOUND = 6;
    private boolean begun;

    private char[][] board;

    /**
     * Instantiates a new Connect four.
     *
     * @param Seeker the seeker
     */
    public ConnectFour(User Seeker) {
        super(Seeker);
        begun = false;
        board = new char[X_BOUND][Y_BOUND];
    }

    /**
     * Instantiates a new Connect four.
     *
     * @param Instigator the instigator
     * @param Challenged the challenged
     * @param first      the first
     */
    public ConnectFour(User Instigator, User Challenged, boolean first) {
        super(Instigator, Challenged);
        begun = true;
        if (first)
            next = A;
        else
            next = B;
        board = new char[X_BOUND][Y_BOUND];
    }

    /**
     * Begins the game
     *
     * @param reaction the reaction trigger
     */
    public void begin(Reaction reaction) {
        B = reaction.getUser();
        begun = true;
        next = random.nextBoolean() ? A : B;
        Message message = reaction.getChannel().getMessageById(String.valueOf(reaction.getMessageId())).complete();
        message.editMessage(String.format("Creating %s's game", message.getMentionedUsers().get(0).getAsMention())).queue();
        for (int i = 0, connectfour_reactionsSize = CONNECTFOUR_REACTIONS.size(); i < connectfour_reactionsSize; i++) {
            if (i != connectfour_reactionsSize - 1)
                message.addReaction(CONNECTFOUR_REACTIONS.get(i)).queue();
            else
                message.addReaction(CONNECTFOUR_REACTIONS.get(i)).complete();
        }
        message.editMessage(getMessage()).queue();
    }

    @Override
    public boolean valid(Reaction reaction) {
        return begun ? CONNECTFOUR_REACTIONS.contains(reaction.getEmoji()) : reaction.getEmoji().equals("⚔");
    }

    @Override
    public void execute(Reaction reaction) {
        super.execute(reaction);
        if (!begun && reaction.getEmoji().equals("⚔")) {
            begin(reaction);
            return;
        } else if (reaction.getUser() == next) {
            int move = CONNECTFOUR_REACTIONS.indexOf(reaction.getEmoji());
            if (move < 0) return;
            for (int y = 0; y < Y_BOUND; y++) {
                if (board[move][y] == '\u0000') {
                    if (reaction.getUser() == A) {
                        board[move][y] = 'a';
                        next = B;
                        break;
                    } else {
                        board[move][y] = 'b';
                        next = A;
                        break;
                    }
                }
            }
        }
        Message message = reaction.getChannel().getMessageById(String.valueOf(reaction.getMessageId())).complete();
        message.editMessage(getMessage()).queue();
        List<MessageReaction> messageReactions = message.getReactions();
        for (MessageReaction mr : messageReactions) {
            if (mr.getEmote().getName().equals(reaction.getEmoji())) {
                mr.removeReaction(reaction.getUser()).queue();
                break;
            }
        }
        if (hasEnded()) {
            setExpired();
            message.editMessage(getMessage()).queue();
        }
    }

    @Override
    public Message getMessage() {
        StringBuilder sb = new StringBuilder();
        for (String emojiNum : CONNECTFOUR_REACTIONS)
            sb.append(emojiNum);
        sb.append("\n");
        for (int y = Y_BOUND - 1; y >= 0; y--) {
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
                    if (x < X_BOUND - 3) {
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
                    if (x < X_BOUND - 3 && y < Y_BOUND - 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                setWinner(token);
                                return true;
                            }
                        }
                    }

                    //checks vertical up
                    if (y < Y_BOUND - 3) {
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
                    if (x > 2 && y < Y_BOUND - 3) {
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
            if (board[x][Y_BOUND - 1] == '\u0000') {
                break;
            } else if (x == 6) {
                winner = samurai;
                return true;
            }
        }
        return false;
    }
}