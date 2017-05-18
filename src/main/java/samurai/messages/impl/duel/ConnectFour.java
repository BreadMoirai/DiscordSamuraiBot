/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.messages.impl.duel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.basic.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.impl.duel.strategy.ConnectFourStrategy;
import samurai.messages.impl.duel.strategy.MiniMaxStrategy;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.ReactionListener;
import samurai.points.PointTracker;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class ConnectFour extends DynamicMessage implements ReactionListener, GenericCommandListener {


    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"));
    private static final String DUEL_REACTION = "\u2694";

    private static final String EMOTE_A = "\uD83D\uDD34";
    private static final String EMOTE_B = "\uD83D\uDD35";

    private static final int X_BOUND = 7, Y_BOUND = 6;

    private final char[][] board;

    private Long userA, userB, winner, next;
    private String avatarA;
    private String avatarB;
    private String nameA, nameB;

    private GameState state;

    private boolean selfOpp;

    private ConnectFourStrategy ai;

    private PointTracker pointTracker;

    public ConnectFour(Member seeking, PointTracker pointTracker) {
        userA = seeking.getUser().getIdLong();
        nameA = seeking.getEffectiveName();
        avatarA = seeking.getUser().getEffectiveAvatarUrl();
        this.pointTracker = pointTracker;
        userB = null;
        nameB = null;
        winner = null;
        board = new char[X_BOUND][Y_BOUND];
        state = new InitialState(this);
    }

    public ConnectFour(Member instigator, Member challenged, PointTracker pointTracker) {
        userA = instigator.getUser().getIdLong();
        nameA = instigator.getEffectiveName();
        avatarA = instigator.getUser().getEffectiveAvatarUrl();
        userB = challenged.getUser().getIdLong();
        nameB = challenged.getEffectiveName();
        avatarB = challenged.getUser().getEffectiveAvatarUrl();
        this.pointTracker = pointTracker;
        next = ThreadLocalRandom.current().nextBoolean() ? userA : userB;
        board = new char[X_BOUND][Y_BOUND];
        state = new BuildState(this);
        selfOpp = challenged.getUser().getIdLong() == Bot.info().ID;
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
        setActive();
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
                .setFooter("SamuraiGames\u2122", Bot.info().AVATAR);
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
                winner = Bot.info().ID;
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
        } else if (next.equals(userB)){
            mb.append(nameA)
                    .append(" ").append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(" <@")
                    .append(userB)
                    .append(">\n");
        } else {
            mb.append(nameA)
                    .append(" ").append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(' ')
                    .append(nameB)
                    .append("\n");
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
                winner = Bot.info().ID;
                break;
        }
    }

    @Override
    public void onCommand(Command command) {
        if (command.getContext().getKey().equalsIgnoreCase("findduel")) {
            final long authorId = command.getContext().getAuthorId();
            if (authorId == userB || authorId == userA) {
                getManager().getClient().getTextChannelById(getChannelId()).sendMessage(state.buildMessage()).queue(((Consumer<Message>) message -> setMessageId(message.getIdLong())).andThen(newMenu(REACTIONS)));
            }
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
            game.selfOpp = user.getIdLong() == Bot.info().ID;
            game.next = ThreadLocalRandom.current().nextBoolean() ? game.userA : game.userB;
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
            final Consumer<Message> after = game.next.equals(Bot.info().ID) ? success -> game.selfMove(success.getJDA(), 0L) : null;
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
            if (event.getUser().getIdLong() != Bot.info().ID)
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
                game.next = 0L;
                MessageBuilder titleMessage = game.buildTitle();
                EmbedBuilder boardEmbed = game.buildBoard();
                if (game.pointTracker != null) {
                    double points = game.pointTracker.transferPoints(game.getGuildId(), game.winner.equals(game.userA) ? game.userB : game.userA, game.winner, PointTracker.DUEL_POINT_RATIO);
                    boardEmbed.addField("The Winner is:", String.format("\uD83C\uDF89<@%d> who gained **%.2f** points from %s", game.winner, points, game.winner.equals(game.userA) ? game.nameB : game.nameA), false).setImage(getWinnerAvatar());
                    event.getChannel().editMessageById(game.getMessageId(), titleMessage.setEmbed(boardEmbed.build()).build()).queue();
                    event.getTextChannel().clearReactionsById(game.getMessageId()).queue();
                } else {
                    event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
                        message.editMessage(titleMessage
                                .setEmbed(boardEmbed
                                        .addField("The Winner is:", String.format("<@%d>", game.winner), false)
                                        .setImage(getWinnerAvatar())
                                        .build())
                                .build())
                                .queue();
                        message.clearReactions().queue();
                    });
                }
                game.unregister();
            } else {
                event.getChannel().getMessageById(event.getMessageId()).queue(message -> message.editMessage(buildMessage()).queue(message1 -> {
                    if (game.selfOpp && game.next.equals(Bot.info().ID)) {
                        game.selfMove(message1.getJDA(), event.getResponseNumber()+1);
                    }
                }));

            }
        }

        private String getWinnerAvatar() {
            if (game.winner.equals(game.userA)) {
                return game.avatarA;
            } else if (game.winner.equals(game.userB)) {
                return game.avatarB;
            } else {
                return Bot.info().AVATAR;
            }
        }
    }

    private void selfMove(JDA jda, long responseNumber) {
        String move;
        if (ai == null) {
            ai = new MiniMaxStrategy(X_BOUND, Y_BOUND, 4);
        }
        final int i = ai.makeMove(board);
        move = REACTIONS.get(i);
        this.onReaction(new MessageReactionAddEvent(jda, responseNumber, jda.getSelfUser(), new MessageReaction(jda.getTextChannelById(getChannelId()), new MessageReaction.ReactionEmote(move, 0L, jda), this.getMessageId(), true, 2)));
    }


}