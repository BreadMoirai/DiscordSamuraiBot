/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.github.breadmoirai.bot.modules.item.responses;

import com.typesafe.config.ConfigFactory;
import com.github.breadmoirai.bot.modules.item.model.database.Inventory;
import com.github.breadmoirai.bot.modules.item.model.Item;
import com.github.breadmoirai.bot.modules.item.model.database.ItemFactory;
import com.github.breadmoirai.bot.framework.core.Response;
import com.github.breadmoirai.bot.framework.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.requests.RequestFuture;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RedPacketDrop extends Response {
    private static final long REACTION = ConfigFactory.load("items").getLong("emote.envelope");
    private static final Color COLOR = new Color(242, 36, 71);
    private static final String TITLE = "\u7ea2\u5305";

    private Instant endTime;
    private int[] drops;
    private int[] dropqueue;
    private int dropsGiven;
    private Set<Long> dropsReceived;
    private transient String dropDisplay;
    private transient RequestFuture<?> scheduledFuture;
    private boolean deleted = false;

    public RedPacketDrop() {
    }

    public RedPacketDrop(Duration duration, int[] drops, int[] dropqueue) {
        this.endTime = Instant.now().plus(duration);
        this.drops = drops;
        this.dropqueue = dropqueue;
        dropsGiven = 0;
        dropsReceived = new HashSet<>(dropqueue.length);
    }

    private String getDropDisplay() {
        if (dropDisplay == null) {
            final Map<Item, Integer> dropMap = new HashMap<>(drops.length / 2 + 1);
            for (int i = 0; i < drops.length - 1; i += 2) {
                final Item itemById = ItemFactory.getItemById(drops[i]);
                if (itemById == null) throw new NullPointerException("Item not found by id: " + drops[i]);
                dropMap.put(itemById, drops[i + 1]);
            }
            dropDisplay = dropMap.entrySet().stream()
                    .sorted(Comparator.comparingInt(value -> value.getKey().getData().getItemId()))
                    .collect(Collectors.groupingBy(entrySet -> entrySet.getKey().getData().getType()))
                    .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .map(itemTypeListEntry -> itemTypeListEntry.stream().flatMap(itemIntegerEntry -> {
                        final String emote = itemIntegerEntry.getKey().getData().getEmote().getAsMention();
                        return IntStream.range(0, itemIntegerEntry.getValue()).mapToObj(value -> emote);
                    }).collect(Collectors.joining())).collect(Collectors.joining("\n"));
        }
        return dropDisplay;
    }

    @Override
    public Message buildMessage() {
        return new MessageBuilder().setEmbed(
                new EmbedBuilder()
                        .setColor(COLOR)
                        .setTitle(TITLE)
                        .addField("Available Gifts", getDropDisplay(), false)
                        .setFooter("Ends At", null)
                        .setTimestamp(endTime)
                        .build()
        ).build();
    }


    @Override
    public void onSend(Message message) {
        message.addReaction(message.getJDA().getEmoteById(REACTION)).queue();
        EventWaiter.get().waitForEvent(GuildMessageReactionAddEvent.class, event -> {
            if (deleted) return true;
            if (event.getMessageIdLong() != getMessageId()) return false;
            if (dropsReceived.add(event.getUser().getIdLong())) {
                Inventory.ofMember(event.getMember()).addItem(ItemFactory.getItemById(dropqueue[dropsGiven++]));
                if (dropsGiven == dropqueue.length) {
                    if (scheduledFuture != null && !scheduledFuture.isDone()) {
                        scheduledFuture.cancel(false);
                    }
                    event.getChannel().editMessageById(getMessageId(), buildEndMessage()).queue();
                    event.getChannel().clearReactionsById(getMessageId()).queue();
                    return true;
                } else {
                    if (scheduledFuture != null && !scheduledFuture.isDone()) {
                        scheduledFuture.cancel(false);
                    }
                    scheduledFuture = event.getChannel().editMessageById(getMessageId(), new EmbedBuilder()
                            .setColor(COLOR)
                            .setTitle(TITLE)
                            .addField("Available Gifts", getDropDisplay(), false)
                            .addField("Gifts claimed", String.valueOf(dropsGiven), false)
                            .setFooter("Ends At", null)
                            .setTimestamp(endTime)
                            .build()).submit();
                }
            }
            return false;
        }, 1, TimeUnit.DAYS, () -> {
            if (scheduledFuture != null && !scheduledFuture.isDone()) {
                scheduledFuture.cancel(false);
            }
            if (!deleted) {
                message.editMessage(buildEndMessage()).queue();
                message.clearReactions().queue();
            }
        });
        EventWaiter.get().waitForEvent(GuildMessageDeleteEvent.class, o -> o.getMessageIdLong() == getMessageId(), 1, TimeUnit.DAYS, () -> deleted = true);
    }

    private Message buildEndMessage() {
        return new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("~~" + TITLE + "~~")
                        .addField("~~Available Gifts~~",
                                IntStream.range(dropsGiven, dropqueue.length).map(operand -> dropqueue[operand]).mapToObj(ItemFactory::getItemById).map(item -> item.getData().getEmote().getAsMention()).collect(Collectors.joining()), false)
                        .addField("Gifts claimed",
                                IntStream.range(0, dropsGiven).map(operand -> dropqueue[operand]).mapToObj(ItemFactory::getItemById).map(item -> item.getData().getEmote().getAsMention()).collect(Collectors.joining()),
                                false)
                        .setFooter("Ended at", null)
                        .setTimestamp(endTime)
                        .build())
                .build();
    }

   /* @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        if (endTime.isAfter(Instant.now())) {
            replace(samuraiDiscord.getMessageManager(), getMessageId());
        } else {
            final TextChannel textChannel = samuraiDiscord.getMessageManager().getClient().getTextChannelById(getChannelId());
            if (textChannel != null) {
                textChannel.editMessageById(getMessageId(), buildEndMessage()).queue(null, ignored -> {
                });
            }
        }
    }*/
}
