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
package samurai.messages.impl.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.listeners.ReactionListener;

import java.util.function.Consumer;

public class Prompt extends DynamicMessage implements ReactionListener, UniqueMessage {

    private static final String YES = "\u2705", NO = "\u274e";

    private final Message prompt;
    private final Consumer<Prompt> onYes, onNo;
    private Message message;

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
        this.message = message;
        message.addReaction(YES).queue();
        message.addReaction(NO).queue();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final long userId = event.getUser().getIdLong();
        if (userId != 0 && userId != this.getAuthorId()) return;
        final String name = event.getReaction().getEmote().getName();
        switch (name) {
            case YES:
                if (onYes != null)
                    onYes.accept(this);
                unregister();
                break;
            case NO:
                if (onNo != null)
                    onNo.accept(this);
                unregister();
                break;
            default:
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

    public Message getMessage() {
        return message;
    }
}
