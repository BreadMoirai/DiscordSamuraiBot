package samurai.messages.dynamic.duel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.messages.base.DynamicMessage;
import samurai.messages.dynamic.duel.strategy.ConnectFourStrategy;
import samurai.messages.dynamic.duel.strategy.MiniMaxStrategy;
import samurai.messages.listeners.ReactionListener;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


/**
 * ConnectFour game
 *
 * @since 4.0
 */
public class ConnectFour extends DynamicMessage implements ReactionListener {


    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"));
    private static final String DUEL_REACTION = "⚔";

    private static final String EMOTE_A = "\uD83D\uDD34";
    private static final String EMOTE_B = "\uD83D\uDD35";

    private static final int X_BOUND = 7, Y_BOUND = 6;

    private static final Random random = new Random();

    private final char[][] board;

    private Long userA, userB, winner, next;
    private String avatarA, avatarB;
    private String nameA, nameB;

    private GameState state;

    private boolean selfOpp;

    private ConnectFourStrategy ai;

    public ConnectFour(User seeking) {
        userA = Long.valueOf(seeking.getId());
        nameA = seeking.getName();
        avatarA = seeking.getEffectiveAvatarUrl();
        userB = null;
        nameB = null;
        winner = null;
        board = new char[X_BOUND][Y_BOUND];
        state = new InitialState(this);
    }

    public ConnectFour(User instigator, User challenged) {
        userA = Long.valueOf(instigator.getId());
        nameA = instigator.getName();
        avatarA = instigator.getEffectiveAvatarUrl();
        userB = Long.valueOf(challenged.getId());
        nameB = challenged.getName();
        avatarB = challenged.getEffectiveAvatarUrl();
        next = random.nextBoolean() ? userA : userB;
        board = new char[X_BOUND][Y_BOUND];
        state = new BuildState(this);
        selfOpp = challenged.getIdLong() == Bot.ID;
    }

    @Override
    protected Message initialize() {
        return state.buildMessage();
    }

    @Override
    protected void onReady(Message message) {
        state.buildConsumer().accept(message);
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        if (state.isValid(event)) state.onReaction(event);
    }

    private EmbedBuilder buildBoard() {
        StringBuilder sb = new StringBuilder();
        for (String emojiNum : REACTIONS)
            sb.append(emojiNum);
        sb.append("\n");
        for (int y = Y_BOUND - 1; y >= 0; y--) {
            for (int x = 0; x < X_BOUND; x++) {
                if (board[x][y] == 'a') {

                    sb.append(EMOTE_A);
                } else if (board[x][y] == 'b') {

                    sb.append(EMOTE_B);
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
                winner = Bot.ID;
                return true;
            }
        }
        return false;
    }

    MessageBuilder buildTitle() {
        MessageBuilder mb = new MessageBuilder();
        if (next.equals(userA)) {
            mb.append("<@").append(userA)
                    .append("> ").append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(' ')
                    .append(nameB)
                    .append("\n");
        } else {
            mb.append(nameA)
                    .append(" ").append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(" <@")
                    .append(userB)
                    .append(">\n");
        }
        return mb;
    }

    private void setWinner(char w) {
        switch (w) {
            case 'a':
                winner = userA;
                break;
            case 'b':
                winner = userB;
                break;
            default:
                winner = Bot.ID;
                break;
        }
    }

    //todo let's make these singleton eh?
    private static abstract class GameState implements ReactionListener {
        ConnectFour game;

        GameState(ConnectFour game) {
            this.game = game;
        }

        abstract boolean isValid(MessageReactionAddEvent event);

        abstract Message buildMessage();

        abstract Consumer<Message> buildConsumer();
    }

    private static class InitialState extends GameState {


        InitialState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(MessageReactionAddEvent event) {
            return event.getReaction().getEmote().getName().equals(DUEL_REACTION)
                    && !game.userA.equals(event.getUser().getIdLong());
        }

        @Override
        Message buildMessage() {
            return new MessageBuilder()
                    .append(String.format("Who is willing to accept <@%d>'s challenge to a perilous game of **Connect Four**", game.userA))
                    .build();
        }

        @Override
        Consumer<Message> buildConsumer() {
            return message -> message.addReaction(DUEL_REACTION).queue();
        }

        @Override
        public void onReaction(MessageReactionAddEvent event) {
            final User user = event.getUser();
            game.userB = user.getIdLong();
            game.nameB = user.getName();
            game.avatarB = user.getEffectiveAvatarUrl();
            game.selfOpp = user.getIdLong() == Bot.ID;
            game.next = random.nextBoolean() ? game.userA : game.userB;
            final BuildState buildState = new BuildState(game);
            game.state = buildState;
            event.getChannel().getMessageById(event.getMessageId()).queue(message -> message.editMessage(buildState.buildMessage()).queue(buildState.buildConsumer()));
        }
    }

    private static class BuildState extends GameState {

        BuildState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(MessageReactionAddEvent event) {
            return false;
        }

        @Override
        Message buildMessage() {
            return new MessageBuilder()
                    .append(String.format("Building <@%d>'s game against <@%d>.", game.userA, game.userB))
                    .build();
        }

        @Override
        Consumer<Message> buildConsumer() {
            final Consumer<Message> after = game.next.equals(Bot.ID) ? success -> game.selfMove(success.getJDA(), 0L) : null;
            return newMenu(REACTIONS).andThen(message -> {
                game.state = new PlayState(game);
                message.editMessage(game.state.buildMessage()).queue(after);
            });
        }

        @Override
        public void onReaction(MessageReactionAddEvent event) {

        }
    }

    private static class PlayState extends GameState {

        PlayState(ConnectFour game) {
            super(game);
        }

        @Override
        boolean isValid(MessageReactionAddEvent event) {
            int i;
            return game.next.equals(Long.valueOf(event.getUser().getId()))
                    && (i = REACTIONS.indexOf(event.getReaction().getEmote().getName())) != -1
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
        public void onReaction(MessageReactionAddEvent event) {
            if (event.getUser().getIdLong() != Bot.ID)
                event.getReaction().removeReaction(event.getUser()).queue();
            int move = REACTIONS.indexOf(event.getReaction().getEmote().getName());
            for (int y = 0; y < Y_BOUND; y++) {
                if (game.board[move][y] == '\u0000') {
                    if (game.userA.equals(event.getUser().getIdLong())) {
                        game.board[move][y] = 'a';
                        game.next = game.userB;
                        break;
                    } else {
                        game.board[move][y] = 'b';
                        game.next = game.userA;
                        break;
                    }
                }
            }
            if (game.hasEnded()) {
                event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
                    message.editMessage(game.buildTitle()
                            .setEmbed(game.buildBoard()
                                    .addField("The Winner is:", String.format("<@%d>", game.winner), false)
                                    .setImage(getWinnerAvatar())
                                    .build())
                            .build())
                            .queue();
                    message.clearReactions().queue();
                });
                game.unregister();
            } else {
                event.getChannel().getMessageById(event.getMessageId()).queue(message -> message.editMessage(buildMessage()).queue());
                if (game.selfOpp && game.next.equals(Bot.ID)) {
                    game.selfMove(event.getJDA(), event.getResponseNumber());
                }
            }
        }

        private String getWinnerAvatar() {
            if (game.winner.equals(game.userA)) {
                return game.avatarA;
            } else if (game.winner.equals(game.userB)) {
                return game.avatarB;
            } else {
                return Bot.AVATAR;
            }
        }
    }

    private void selfMove(JDA jda, long responseNumber) {
        String move;
        if (ai == null) {
            ai = new MiniMaxStrategy(X_BOUND, Y_BOUND, 3);
        }
        final int i = ai.makeMove(board);
        move = REACTIONS.get(i);
        this.onReaction(new MessageReactionAddEvent(jda, responseNumber, jda.getSelfUser(), new MessageReaction(jda.getTextChannelById(getChannelId()), new MessageReaction.ReactionEmote(move, 0L, jda), this.getMessageId(), true, 2)));
    }


}