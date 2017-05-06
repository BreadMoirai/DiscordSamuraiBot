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
package samurai.messages.impl.poker;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PokerBuilder extends DynamicMessage implements UniqueMessage {
    private static final String[] BUILD_MESSAGES = new String[]{"Stacking the odds...", "Building a new Poker Table...", "Shuffling cards...", "Clearing the dust..."};
    private ChannelAction tableAction;
    private ChannelAction[] pokerChannels;
    private ChannelAction logAction;
    private Table table;

    public PokerBuilder(ChannelAction tableAction, ChannelAction[] pokerChannels, ChannelAction logAction) {
        this.tableAction = tableAction;
        this.pokerChannels = pokerChannels;
        this.logAction = logAction;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append(BUILD_MESSAGES[ThreadLocalRandom.current().nextInt(3)]).build();
    }

    @Override
    protected void onReady(Message message) {
        tableAction.queue(channel -> {
            table = new Table();
            table.setChannelId(channel.getIdLong());
            for (ChannelAction pokerChannel : pokerChannels) {
                pokerChannel.queue(channel1 -> {
                    final Seat seat = new Seat(table);
                    seat.setChannelId(channel1.getIdLong());
                    seat.send(getManager());
                });
            }
            logAction.queue(channel1 -> {
                final TableLog tableLog = new TableLog();
                tableLog.setChannelId(channel1.getIdLong());
                tableLog.send(getManager());
                table.setLog(tableLog);
            });
            table.send(getManager());
            table.setEndAction(this::unregister);
            unregister();
            final TextChannel sourceChannel = channel.getGuild().getTextChannelById(getChannelId());
            sourceChannel.deleteMessageById(getMessageId()).queue();
            sourceChannel.sendMessage("The Table has been successfully constructed over at " + ((TextChannel) channel).getAsMention()).queue(message1 -> {
                setMessageId(message1.getIdLong());
                register();
            });
        });
    }


    @Override
    public MessageScope scope() {
        return MessageScope.Guild;
    }

    @Override
    public boolean shouldPrompt() {
        return true;
    }

    @Override
    public Message prompt() {
        return new MessageBuilder().append("A Poker Table has already been built. Destroy the previous and build a new one?").build();
    }

    @Override
    public void close(TextChannel channel) {
        if (table != null) table.destroy();
        channel.editMessageById(getMessageId(), new MessageBuilder().append("This has table has been destroyed.").build()).queue();
    }

    @Override
    public boolean isExpired() {
        return false;
    }

}
