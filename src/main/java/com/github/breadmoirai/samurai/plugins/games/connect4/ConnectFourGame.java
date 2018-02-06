/*
 *     Copyright 2017-2018 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.breadmoirai.samurai.plugins.games.connect4;

import com.github.breadmoirai.breadbot.plugins.waiter.EventActionFuture;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.Dispatchable;
import com.github.breadmoirai.samurai.plugins.games.connect4.strategy.ConnectFourStrategy;
import javafx.util.Pair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class ConnectFourGame implements Dispatchable {

    public static final int X_BOUND = 7, Y_BOUND = 6;
    public static final String DUEL_REACTION = "\u2694";
    private static final String[] REACTIONS = new String[]{"1\u20e3", "2\u20e3", "3\u20e3", "4\u20e3", "5\u20e3", "6\u20e3", "7\u20e3"};
    private static final String EMOTE_A = "\uD83D\uDD34";
    private static final String EMOTE_B = "\uD83D\uDD35";

    private final char[][] board;
    private final Member playerA;
    private final ConnectFourStrategy strategy;
    private final Member playerB;
    private final long messageId;
    private final EventWaiter waiter;
    private final BiConsumer<Pair<Member, Member>, EmbedBuilder> onWin;
    private final String selfAvatar;

    private Member next;
    private Member winner;


    public ConnectFourGame(Member playerA, Member challenged, long messageId, EventWaiter waiter, BiConsumer<Pair<Member, Member>, EmbedBuilder> onWin) {
        this.playerA = playerA;
        this.playerB = challenged;
        this.messageId = messageId;
        this.waiter = waiter;
        this.onWin = onWin;
        next = ThreadLocalRandom.current().nextBoolean() ? playerA : challenged;
        board = new char[X_BOUND][Y_BOUND];
        strategy = null;
        selfAvatar = playerA.getJDA().getSelfUser().getAvatarUrl();
    }

    public ConnectFourGame(Member playerA, ConnectFourStrategy strategy, long messageId, EventWaiter waiter, BiConsumer<Pair<Member, Member>, EmbedBuilder> onWin) {
        this.playerA = playerA;
        this.strategy = strategy;
        this.playerB = playerA.getGuild().getSelfMember();
        this.messageId = messageId;
        this.waiter = waiter;
        this.onWin = onWin;
        next = ThreadLocalRandom.current().nextBoolean() ? playerA : null;
        board = new char[X_BOUND][Y_BOUND];
        selfAvatar = playerA.getJDA().getSelfUser().getAvatarUrl();
    }

    @Override
    public void dispatch(TextChannel channel) {
        channel.editMessageFormatById(messageId, "Building %s's game against %s.", playerA, playerB)
                .queue(this::setupReactions);
    }

    public void setupReactions(Message message) {
        for (int i = 0; i < REACTIONS.length; i++) {
            if (i != REACTIONS.length - 1) {
                message.addReaction(REACTIONS[i]).queue();
            } else {
                message.addReaction(REACTIONS[i]).queue(aVoid -> message
                        .editMessage(buildTitle()
                                .setEmbed(buildBoard().build())
                                .build())
                        .queue(editMessage -> waitForReactions(message)));
            }
        }
    }

    public EventActionFuture<Void> waitForReactions(Message message) {
        return waiter
                .waitForReaction()
                .withName(REACTIONS)
                .on(message)
                .matching(this::isValid)
                .action(this::onReaction)
                .stopIf((reactionEvent, j) -> hasEnded())
                .finish(() -> onFinish(message))
                .build();
    }

    public void onFinish(Message message) {
        next = null;
        final EmbedBuilder embedBuilder = buildBoard();
        onWin.accept(new Pair<>(winner, winner.equals(playerA) ? playerB : playerA), embedBuilder);
        message.editMessage(buildTitle()
                .setEmbed(embedBuilder.build())
                .build())
                .queue();
        message.clearReactions().queue();
    }

    public void onReaction(GenericMessageReactionEvent reactionEvent) {
        reactionEvent.getReaction().removeReaction().queue();
        final Member member = reactionEvent.getMember();
        int move = getReactionIdx(reactionEvent.getReactionEmote().getName());
        makeMove(member, move);
        if (strategy != null) {
            final int i = strategy.makeMove(board);
            makeMove(playerB, i);
            next = playerA;
        }
    }

    public void makeMove(Member member, int move) {
        for (int y = 0; y < Y_BOUND; y++) {
            if (board[move][y] == '\u0000') {
                if (playerA.equals(member)) {
                    board[move][y] = 'a';
                    next = playerB;
                    break;
                } else {
                    board[move][y] = 'b';
                    next = playerA;
                    break;
                }
            }
        }
    }


    private boolean isValid(GenericMessageReactionEvent event) {
        int i;
        return next.equals(event.getMember())
                && (i = getReactionIdx(event.getReactionEmote().getName())) != -1
                && board[i][Y_BOUND - 1] == '\u0000';
    }

    private int getReactionIdx(String name) {
        for (int i = 0; i < REACTIONS.length; i++) {
            if (REACTIONS[i].equals(name)) return i;
        }
        return -1;
    }

    MessageBuilder buildTitle() {
        MessageBuilder mb = new MessageBuilder();
        if (playerA.equals(next)) {
            mb.append(playerA)
                    .append(' ').append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(' ')
                    .append(playerB.getEffectiveName())
                    .append('\n');
        } else if (playerB.equals(next)) {
            mb.append(playerA.getEffectiveName())
                    .append(' ').append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B)
                    .append(playerB)
                    .append('\n');
        } else {
            mb.append(playerA.getEffectiveName())
                    .append(" ").append(EMOTE_A).append(" \uD83C\uDD9A ").append(EMOTE_B).append(' ')
                    .append(playerB.getEffectiveName())
                    .append('\n');
        }
        return mb;
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
                .setFooter("SamuraiGames\u2122", selfAvatar);
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
                setWinner('x');
                return true;
            }
        }
        return false;
    }

    private void setWinner(char w) {
        switch (w) {
            case 'a':
                winner = playerA;
                break;
            case 'b':
                winner = playerB;
                break;
            case 'x':
                winner = playerA.getGuild().getSelfMember();
                break;
        }
    }

}