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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ReactionListener;
import samurai.util.MessageUtil;

import java.util.List;

/**
 * @author TonTL
 * @version 3/16/2017
 */
public class HangmanHelper extends DynamicMessage implements ReactionListener {


    private HangmanGame hangman;
    private List<String> reactions;

    HangmanHelper(HangmanGame hangman, List<String> reactions) {
        this.hangman = hangman;
        this.reactions = reactions;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Click a letter to start guessing.").build();
    }

    @Override
    protected void onReady(Message message) {
        MessageUtil.addReaction(message, reactions);
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        hangman.onReaction(event);
    }

    void kill(MessageChannel channel) {
        unregister();
        channel.getMessageById(String.valueOf(getMessageId())).queue(message -> message.delete().queue());
    }
}
