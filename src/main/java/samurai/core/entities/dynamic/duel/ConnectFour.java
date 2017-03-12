package samurai.core.entities.dynamic.duel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import samurai.core.Bot;
import samurai.core.entities.base.DynamicMessage;
import samurai.core.events.ReactionEvent;
import samurai.core.events.listeners.ReactionListener;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


/**
 * ConnectFour game
 *
 * @since 4.0
 */
public class ConnectFour extends DynamicMessage implements ReactionListener {


    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"));
    private static final String DUEL_REACTION = "⚔";

    private static final int X_BOUND = 7, Y_BOUND = 6;

    private final char[][] board;

    private Long A, B, winner, next;
    private String nameA, nameB;

    private GameState state;

    public ConnectFour(User seeking) {
        A = Long.valueOf(seeking.getId());
        nameA = seeking.getName();
        B = null;
        nameB = null;
        winner = null;
        board = new char[X_BOUND][Y_BOUND];
        state = new InitialState(this);
    }

    public ConnectFour(User instigator, User challenged) {
        A = Long.valueOf(instigator.getId());
        nameA = instigator.getName();
        B = Long.valueOf(challenged.getId());
        nameB = challenged.getName();
        next = Game.random.nextBoolean() ? A : B;
        board = new char[X_BOUND][Y_BOUND];
        state = new PlayState(this);
    }

    private EmbedBuilder buildBoard() {
        StringBuilder sb = new StringBuilder();
        for (String emojiNum : REACTIONS)
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
    protected void onReady() {
        submitNewMessage(state.buildMessage(), state.buildConsumer());
    }

    @Override
    public void onReaction(ReactionEvent event) {
        if (state.isValid(event)) state.onReaction(event);
    }

    private boolean hasEnded() {
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
                winner = Long.valueOf(Bot.ID);
                return true;
            }
        }
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

    private void setWinner(char w) {
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

    //todo let's make these singleton eh?
    private static abstract class GameState implements ReactionListener {
        ConnectFour game;

        GameState(ConnectFour game) {
            this.game = game;
        }

        abstract boolean isValid(ReactionEvent event);

        abstract Message buildMessage();

        abstract Consumer<Message> buildConsumer();
    }

    private static class InitialState extends GameState {


        InitialState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(ReactionEvent event) {
            return event.getName().equals(DUEL_REACTION)
                    && !game.A.equals(event.getUserId());
        }

        @Override
        Message buildMessage() {
            return new MessageBuilder()
                    .append(String.format("Who is willing to accept <@%d>'s challenge to a perilous game of **Connect Four**", game.A))
                    .build();
        }

        @Override
        Consumer<Message> buildConsumer() {
            return message -> message.addReaction(DUEL_REACTION).queue();
        }

        @Override
        public void onReaction(ReactionEvent event) {
            game.B = event.getUserId();
            game.nameB = Bot.getUser(event.getUserId()).getName();
            game.next = Game.random.nextBoolean() ? game.A : game.B;
            final BuildState buildState = new BuildState(game);
            game.state = buildState;
            game.updateMessage(buildState.buildMessage(), buildState.buildConsumer(), null);
        }
    }

    private static class BuildState extends GameState {

        BuildState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(ReactionEvent event) {
            return false;
        }

        @Override
        Message buildMessage() {
            return new MessageBuilder()
                    .append(String.format("Building <@%d>'s game against <@%d>.", game.A, game.B))
                    .build();
        }

        @Override
        Consumer<Message> buildConsumer() {
            return newMenu(REACTIONS).andThen(message -> {
                game.state = new PlayState(game);
                message.editMessage(game.state.buildMessage()).queue();
            });
        }

        @Override
        public void onReaction(ReactionEvent event) {

        }
    }

    private static class PlayState extends GameState {

        PlayState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(ReactionEvent event) {
            int i;
            return game.next.equals(event.getUserId())
                    && (i = REACTIONS.indexOf(event.getName())) != -1
                    && game.board[i][Y_BOUND - 1] == '\u0000';
        }

        @Override
        Message buildMessage() {
            return game.buildTitle().setEmbed(game.buildBoard().build()).build();
        }

        @Override
        Consumer<Message> buildConsumer() {
            return null;
        }

        @Override
        public void onReaction(ReactionEvent event) {
            game.removeReaction(game.getMessageId(), event.getUserId(), event.getName());
            int move = REACTIONS.indexOf(event.getName());
            for (int y = 0; y < Y_BOUND; y++) {
                if (game.board[move][y] == '\u0000') {
                    if (game.A.equals(event.getUserId())) {
                        game.board[move][y] = 'a';
                        game.next = game.B;
                        break;
                    } else {
                        game.board[move][y] = 'b';
                        game.next = game.A;
                        break;
                    }
                }
            }
            if (game.hasEnded()) {
                game.updateMessage(game.buildTitle()
                        .setEmbed(game.buildBoard()
                                .addField("The Winner is:", String.format("<@%d>", game.winner), false)
                                .setImage(Bot.getUser(game.winner).getAvatarUrl())
                                .build())
                        .build());
                game.clearReactions();
                game.unregister();
            } else
                game.updateMessage(buildMessage());
        }
    }


}