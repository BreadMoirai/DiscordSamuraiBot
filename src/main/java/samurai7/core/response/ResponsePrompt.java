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
package samurai7.core.response;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.SamuraiDiscord;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.impl.util.Prompt;

import java.util.function.Consumer;

public class ResponsePrompt extends DynamicResponse implements Reloadable{
    private static final long YES_ID, NO_ID;

    static {
        final Config config = ConfigFactory.load("source_commands.conf");
        YES_ID = config.getLong("prompt.yes");
        NO_ID = config.getLong("prompt.no");
    }

    private final Message prompt;
    private final Consumer<ResponsePrompt> onYes;
    private final Consumer<ResponsePrompt> onNo;
    private JDA client;

    public ResponsePrompt(Message prompt, Consumer<ResponsePrompt> onYes, Consumer<ResponsePrompt> onNo) {
        this.prompt = prompt;
        this.onYes = onYes;
        this.onNo = onNo;
    }

    @Override
    public Message getMessage() {
        return prompt;
    }

    @Override
    public void onSend(Message message) {
        this.client = message.getJDA();
        message.addReaction(message.getJDA().getEmoteById(YES_ID)).queue();
        message.addReaction(message.getJDA().getEmoteById(NO_ID)).queue();
    }

    @Override
    public void onGenericGuildMessageReactionEvent(GenericGuildMessageReactionEvent event) {
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

}
