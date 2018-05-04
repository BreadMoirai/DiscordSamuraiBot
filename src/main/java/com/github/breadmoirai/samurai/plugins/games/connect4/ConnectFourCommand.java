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

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Author;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.breadbot.util.Emoji;
import com.github.breadmoirai.samurai.plugins.games.connect4.strategy.MiniMaxStrategy;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import javafx.util.Pair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConnectFourCommand {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};
    private static final String CROSSED_SWORDS = Emoji.CROSSED_SWORDS.getUtf8();
    private static final String SHIELD = Emoji.SHIELD.getUtf8();

    @MainCommand
    public ConnectFourGame connect4(CommandEvent event,
                                    @Author Member player,
                                    Member opponent,
                                    OptionalDouble wager,
                                    DerbyPointPlugin plugin, EventWaiter waiter) {
        double pointWager = wager.orElse(1);
        if (event.requirePermission(PERMISSIONS)) {
            return null;
        }
        if (opponent == null) {
            event.reply("Who is willing to accept ")
                    .append(player)
                    .append("'s challenge to a perilous game of **Connect Four**")
                    .onSuccess(openChallenge(event, player, waiter, pointWager, plugin)).send();

        } else if (opponent.getUser().equals(event.getJDA().getSelfUser())) {
            return new ConnectFourGame(player, new MiniMaxStrategy(ConnectFourGame.X_BOUND, ConnectFourGame.Y_BOUND, 4),
                                       0L, waiter, onWin(pointWager, plugin));
        } else {
            event.reply()
                    .append(opponent)
                    .append(", are you willing to accept ")
                    .append(player)
                    .append("'s challenge to a perilous game of **Connect Four**")
                    .onSuccess(closedChallenge(event, player, opponent, waiter, pointWager, plugin)).send();
        }
        return null;
    }

    private BiConsumer<Pair<Member, Member>, EmbedBuilder> onWin(double wager, DerbyPointPlugin plugin) {
        return (pair, embed) -> {
            embed.addField("The Winner is **" + pair.getKey().getAsMention() + "**",
                           pair.getKey().getEffectiveName() + " gains **" + wager + "** points from " + pair.getValue()
                                   .getEffectiveName(), false);
            embed.setImage(pair.getKey().getUser().getEffectiveAvatarUrl());
            plugin.offsetPoints(pair.getKey().getUser().getIdLong(), wager);
            plugin.offsetPoints(pair.getValue().getUser().getIdLong(), wager * -1);
        };
    }

    @NotNull
    private Consumer<Message> closedChallenge(CommandEvent event, @Author Member player, Member opponent,
                                              EventWaiter waiter, double pointWager, DerbyPointPlugin points) {
        return message -> {
            message.addReaction(CROSSED_SWORDS).queue();
            message.addReaction(Emoji.SHIELD.getUtf8()).queue();
            waiter.waitForReaction()
                    .matching(e -> e.getMember().equals(opponent))
                    .on(message)
                    .withName(CROSSED_SWORDS, SHIELD)
                    .action(reaction -> {
                        final String name = reaction.getReactionEmote().getName();
                        if (name.equals(SHIELD)) {
                            message.editMessageFormat("**%s**'s challenge to **%s** has been denied",
                                                      player.getEffectiveName(), opponent.getEffectiveName()).queue();
                        } else {
                            new ConnectFourGame(player, reaction.getMember(), message.getIdLong(), waiter,
                                                onWin(pointWager, points))
                                    .dispatch(event.getChannel());
                        }
                    })
                    .build();
        };
    }

    @NotNull
    private Consumer<Message> openChallenge(CommandEvent event, @Author Member player, EventWaiter waiter,
                                            double pointWager, DerbyPointPlugin points) {
        return message -> {
            message.addReaction(CROSSED_SWORDS).queue();
            waiter.waitForReaction()
                    .matching(e -> !e.getUser().isBot())
                    .matching(e -> !e.getMember().equals(player))
                    .on(message)
                    .withName(CROSSED_SWORDS)
                    .action(reaction -> {
                        new ConnectFourGame(player, reaction.getMember(), message.getIdLong(), waiter,
                                            onWin(pointWager, points))
                                .dispatch(event.getChannel());
                    })
                    .build();
        };
    }
}
