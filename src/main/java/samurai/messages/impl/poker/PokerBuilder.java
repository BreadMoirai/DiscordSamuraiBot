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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PokerBuilder extends DynamicMessage implements UniqueMessage {
    private final TextChannel[] pokerChannels;
    private TextChannel sourceChannel;

    public PokerBuilder(TextChannel[] pokerChannels) {
        this.pokerChannels = pokerChannels;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Building a new Poker Table...").build();
    }

    @Override
    protected void onReady(Message message) {
        this.sourceChannel = message.getTextChannel();
        sourceChannel.sendTyping().queue(CheckCompletion(pokerChannels));
    }

    private Consumer<Void> CheckCompletion(TextChannel[] channels) {
        return aVoid -> {
            boolean complete = true;
            for (TextChannel channel : channels) {
                if (channel == null) {
                    complete = false;
                    break;
                }
            }
            if (!complete) {
                sourceChannel.sendTyping().queueAfter(5, TimeUnit.SECONDS, CheckCompletion(channels));
            } else {
                openTable(channels);
            }
        };
    }

    private void openTable(TextChannel[] tableChannels) {
        final Table table = new Table();
        table.setChannelId(tableChannels[0].getIdLong());
        for (int i = 1; i < tableChannels.length - 1; i++) {
            final Seat seat = new Seat();
            seat.setChannelId(tableChannels[i].getIdLong());
            table.addSeat(seat);
        }
        final TableLog tableLog = new TableLog();
        tableLog.setChannelId(tableChannels[tableChannels.length - 1].getIdLong());
        table.setLog(tableLog);
        table.send(getManager());
        table.setEndAction(this::unregister);
        sourceChannel.editMessageById(getMessageId(), "The Table has been successfully constructed over at " + tableChannels[0].getAsMention());
        sourceChannel = null;
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
        for (TextChannel pokerChannel : pokerChannels) {
            pokerChannel.delete().queue();
        }
        channel.editMessageById(getMessageId(), new MessageBuilder().append("This has table has been destroyed.").build()).queue();
    }

    @Override
    public boolean isExpired() {
        return false;
    }

}
