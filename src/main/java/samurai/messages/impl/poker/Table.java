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

import groovyjarjarantlr.debug.MessageListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import samurai.command.fun.Poker;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ChannelMessageListener;

import java.util.ArrayList;
import java.util.List;

public class Table extends DynamicMessage implements ChannelMessageListener {

    private List<Seat> seats;
    private TableLog log;
    private TextChannel channel;
    private Runnable endAction;

    {
        seats = new ArrayList<>(8);
    }

    @Override
    protected Message initialize() {
        for (Seat seat : seats) {
            seat.send(getManager());
        }
        return new MessageBuilder().append("**Welcome to Samurai's Poker Game and I will be your dealer. Please, take a seat and we can begin.**").build();
    }

    @Override
    protected void onReady(Message message) {
        channel = message.getTextChannel();
    }

    private Message getAvailableSeats() {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Available Seats", null);
        eb.setColor(channel.getGuild().getSelfMember().getColor());
        for (Seat seat : seats) {
            eb.appendDescription(seat.getChannel().getTopic()).appendDescription(" ");
            if (seat.isAvailable()) {
                eb.appendDescription(seat.getChannel().getAsMention()).appendDescription("\n");
            } else {
                eb.appendDescription(seat.getPatron().getEffectiveName()).appendDescription("\n");
            }
        }
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    void addSeat(Seat seat) {
        seats.add(seat);
        if (seats.size() == Poker.SEAT_COUNT) {
            channel.sendMessage(getAvailableSeats()).queue();
        }
    }

    void setLog(TableLog log) {
        this.log = log;
    }

    void setEndAction(Runnable endAction) {
        this.endAction = endAction;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public void destroy() {
        for (Seat seat : seats) {
            seat.destroy();
        }
        log.destroy();
        endAction.run();
        channel.delete().queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        destroy();
    }
}
