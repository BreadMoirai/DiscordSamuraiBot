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
package com.github.breadmoirai.samurai.messages.impl.util;

import com.github.breadmoirai.samurai.messages.base.DynamicMessage;
import com.github.breadmoirai.samurai.messages.base.UniqueMessage;
import com.github.breadmoirai.samurai.messages.listeners.ReactionListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.function.Consumer;

public class Prompt extends DynamicMessage implements ReactionListener, UniqueMessage {

    private static final long YES_ID, NO_ID;

    static {
        final Config config = ConfigFactory.load("source_commands.conf");
        YES_ID = config.getLong("prompt.yes");
        NO_ID = config.getLong("prompt.no");
    }


    private final Message prompt;
    private final Consumer<Prompt> onYes, onNo;
    private JDA client;

    public Prompt(Message prompt, Consumer<Prompt> onYes, Consumer<Prompt> onNo) {
        this.prompt = prompt;
        this.onYes = onYes;
        this.onNo = onNo;
    }


    @Override
    protected Message initialize() {
        return prompt;
    }

    @Override
    protected void onReady(Message message) {
        this.client = message.getJDA();
        message.addReaction(message.getJDA().getEmoteById(YES_ID)).queue();
        message.addReaction(message.getJDA().getEmoteById(NO_ID)).queue();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final long userId = event.getUser().getIdLong();
        if (userId != 0 && userId != this.getAuthorId()) return;
        final long emoteId = event.getReaction().getEmote().getIdLong();
        if (emoteId == YES_ID) {
            if (onYes != null)
                onYes.accept(this);
            unregister();
        } else if (emoteId == NO_ID) {
            if (onNo != null)
                onNo.accept(this);
            unregister();
        }

    }

    @Override
    public boolean shouldPrompt() {
        return false;
    }

    @Override
    public void close(TextChannel channel) {
        channel.deleteMessageById(getMessageId()).queue();
    }

    public JDA getJDA() {
        return client;
    }

    public TextChannel getChannel() {
        return client.getTextChannelById(getChannelId());
    }
}
