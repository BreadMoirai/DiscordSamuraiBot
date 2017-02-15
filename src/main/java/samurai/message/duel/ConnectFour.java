package samurai.message.duel;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import samurai.Bot;
import samurai.action.Reaction;
import samurai.message.MessageEdit;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;


/**
 * ConnectFour game
 *
 * @since 4.0
 */
public class ConnectFour extends Game {


    public static final List<String> CONNECTFOUR_REACTIONS = new UnmodifiableArrayList<>(new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"}, 7);

    private static final int X_BOUND = 7, Y_BOUND = 6;
    private boolean begun, initialized;

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
        initialized = false;
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
        next = first ? A : B;
        board = new char[X_BOUND][Y_BOUND];
        initialized = false;
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
    }

    @Override
    public boolean valid(Reaction reaction) {
        return begun ? CONNECTFOUR_REACTIONS.contains(reaction.getEmoji()) : reaction.getEmoji().equals("⚔");
    }

    @Override
    public MessageEdit call() throws Exception {
        execute(getReaction());
        return new MessageEdit(getChannelId(), getMessageId(), getMessage()).setSuccessConsumer(!initialized ? initReactionMenu() : message -> {
            for (MessageReaction reaction : message.getReactions()) {
                if (reaction.getEmote().getName().equals(getReaction().getEmoji())) {
                    reaction.removeReaction(getReaction().getUser());
                    return;
                }
            }
        });
    }

    @Override
    public void execute(Reaction reaction) {
        super.execute(reaction);
        if (!begun && reaction.getEmoji().equals("⚔")) {
            begin(reaction);
            return;
        }
        if (reaction.getUser() == next) {
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
        if (hasEnded()) {
            setExpired();
        }

    }

    @Override
    public Consumer<Message> getConsumer() {
        if (begun)
            return message -> {
                super.getConsumer().accept(message);
                initReactionMenu().accept(message);
            };
        else
            return message -> {
                super.getConsumer().accept(message);
                message.addReaction("⚔").queue();
            };
    }

    private Consumer<Message> initReactionMenu() {
        System.out.println("Initializing a ConnectFourGame");
        initialized = true;
        return message -> {
            message.editMessage(String.format("Building %s's game against %s", A.getAsMention(), B.getAsMention()));
            for (String s : CONNECTFOUR_REACTIONS) {
                if (!s.equals(CONNECTFOUR_REACTIONS.get(CONNECTFOUR_REACTIONS.size() - 1)))
                    message.addReaction(s).queue();
                else
                    message.addReaction(s).queue(success -> message.editMessage(getMessage()).queue());
            }
        };
    }

    @Override
    public Message getMessage() {
        if (!begun)
            return new MessageBuilder().append(String.format("Who is willing to accept %s's challenge to a perilous game of **Connect Four**", A.getAsMention())).build();
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
                winner = Bot.self;
                return true;
            }
        }
        return false;
    }
}