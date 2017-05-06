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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ReactionListener;

public class Seat extends DynamicMessage implements ReactionListener{

    private TextChannel channel;
    private Member patron;
    private Table table;

    public Seat(Table table) {
        this.table = table;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("This Table isn't ready yet.").build();
    }

    @Override
    protected void onReady(Message message) {
        channel = message.getTextChannel();
        message.editMessage(new MessageBuilder().setEmbed(new EmbedBuilder().appendDescription("Click ").appendDescription(channel.getTopic()).appendDescription(" to take a seat").build()).build()).queue();
        message.addReaction(channel.getTopic()).queue();
        table.addSeat(this);
    }

    void destroy() {
        channel.delete().queue();
    }

    boolean isAvailable() {
        return getPatron() == null;
    }

    Member getPatron() {
        return patron;
    }

    TextChannel getChannel() {
        return channel;
    }

    @Override
    public boolean isExpired() {
        return false;
    }
    @Override
    public void onReaction(MessageReactionAddEvent event) {

    }
}
