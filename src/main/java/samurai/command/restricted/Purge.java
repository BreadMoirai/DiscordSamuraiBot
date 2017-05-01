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
package samurai.command.restricted;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;

/**
 * Will delete this command upon public release
 * @author TonTL
 * @version 3/16/2017
 */
@Key("purge")
@Source
@Creator
public class Purge extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final TextChannel channel = context.getChannel();
        final MessageHistory history = channel.getHistory();
        if (context.isNumeric()) {
            int i = Integer.parseInt(context.getContent());
            while (i > 100) {
                history.retrievePast(100).queue(messages -> channel.deleteMessages(messages).queue());
                i -= 100;
            }
            history.retrievePast(i).queue(messages -> channel.deleteMessages(messages).queue());
        } else {
            history.retrievePast(100).queue(messages -> {
                if (messages.size() < 3)
                    messages.stream().mapToLong(Message::getIdLong).mapToObj(channel::deleteMessageById).forEach(RestAction::queue);
                else
                    channel.deleteMessages(messages).queue();
            });
        }
        return null;
    }
}
