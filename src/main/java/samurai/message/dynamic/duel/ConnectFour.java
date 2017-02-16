package samurai.message.dynamic.duel;

import com.sun.javafx.UnmodifiableArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import samurai.Bot;
import samurai.message.modifier.Reaction;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;


/**
 * ConnectFour game
 *
 * @since 4.0
 */
public class ConnectFour extends Game {


    private static final List<String> CONNECTFOUR_REACTIONS = new UnmodifiableArrayList<>(new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"}, 7);
    private static final String DUEL_REACTION = "⚔";

    private static final int X_BOUND = 7, Y_BOUND = 6;

    private char[][] board;

    public ConnectFour(User Seeker) {
        super(Seeker);
        board = new char[X_BOUND][Y_BOUND];
        setStage(0);
    }

    public ConnectFour(User Instigator, User Challenged) {
        super(Instigator, Challenged);
        next = Game.random.nextBoolean() ? A : B;
        board = new char[X_BOUND][Y_BOUND];
        setStage(1);
    }

    @Override
    public boolean valid(Reaction reaction) {
        switch (getStage()) {
            case 0:
                return reaction.getName().equals("⚔") && reaction.getUser() != A;
            case 2:
                return CONNECTFOUR_REACTIONS.contains(reaction.getName()) && reaction.getUser() == next;
            default:
                return false;
        }
    }


    @Override
    protected void execute() {
        switch (getStage()) {
            case 0:
                B = getReaction().getUser();
                next = Game.random.nextBoolean() ? A : B;
                setStage(1);
                break;
            case 2:
                int move = CONNECTFOUR_REACTIONS.indexOf(getReaction().getName());
                for (int y = 0; y < Y_BOUND; y++) {
                    if (board[move][y] == '\u0000') {
                        if (getReaction().getUser() == A) {
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
                if (hasEnded()) {
                    setStage(3);
                }
                break;
            default:
                new IllegalAccessException(String.format("[%d]Game is accessed when no action should take place.", this.getMessageId())).printStackTrace();
        }
    }

    @Override
    public Message getMessage() {
        switch (getStage()) {
            case 0:
                return new MessageBuilder()
                        .append(String.format("Who is willing to accept %s's challenge to a perilous game of **Connect Four**", A.getAsMention()))
                        .append(String.format("%nSTAGE: **%d/%d**", getStage(), getLastStage()))
                        .build();
            case 1:
                return new MessageBuilder()
                        .append(String.format("Building %s's game against %s.", A.getAsMention(), B.getAsMention()))
                        .append(String.format("%nSTAGE: **%d/%d**", getStage(), getLastStage()))
                        .build();
            case 2:
                return buildTitle()
                        .setEmbed(buildBoard()
                                .build())
                        .append(String.format("%nSTAGE: **%d/%d**", getStage(), getLastStage()))
                        .build();
            case 3:
                return buildTitle()
                        .setEmbed(buildBoard()
                                .addField("The Winner is:", winner.getName(), false)
                                .setImage(winner.getAvatarUrl())
                                .build())
                        .append(String.format("%nSTAGE: **%d/%d**", getStage(), getLastStage()))
                        .build();
            default:
                return null;
        }
    }

    private EmbedBuilder buildBoard() {
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
        return new EmbedBuilder()
                .addField("Connect 4", sb.toString(), true)
                .setColor(Color.BLACK)
                .setFooter("SamuraiGames\u2122", Bot.AVATAR);
    }

    @Override
    public Consumer<Message> getConsumer() {
        switch (getStage()) {
            case 0:
                return message -> message.addReaction(DUEL_REACTION).queue();
            case 1:
                return initReactionMenu();
            case 2:
                return getEditConsumer();
            case 3:
                return message -> {
                    for (MessageReaction mr : message.getReactions()) {
                        if (CONNECTFOUR_REACTIONS.contains(mr.getEmote().getName()) || mr.getEmote().getName().equals(DUEL_REACTION)) {
                            mr.removeReaction().queue();
                        }
                    }
                };
            default:
                return null;

        }
    }

    private Consumer<Message> initReactionMenu() {
        return message -> {
            message.editMessage(String.format("Building %s's game against %s", A.getAsMention(), B.getAsMention()));
            for (String s : CONNECTFOUR_REACTIONS) {
                if (!s.equals(CONNECTFOUR_REACTIONS.get(CONNECTFOUR_REACTIONS.size() - 1)))
                    message.addReaction(s).queue();
                else
                    message.addReaction(s).queue(success -> message.editMessage(getMessage()).queue(success2 -> setStage(2)));
            }
        };
    }


    @Override
    protected int getLastStage() {
        return 3;
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