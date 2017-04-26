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
package samurai.command.osu;

import net.dv8tion.jda.core.entities.Message;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("upload")
public class Upload extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        List<Message.Attachment> attaches = context.getAttaches();
        if (context.getAttaches().size() != 1 || !context.getAttaches().get(0).getFileName().endsWith(".db")) {
            return FixedMessage.build("? No valid attachment found.");
        } else if (context.getAttaches().get(0).getFileName().equalsIgnoreCase("scores.db")) {
            return null;
            //fixme
                    //new ConflictMerge(SamuraiStore.readScores(SamuraiStore.downloadFile(attaches.get(0))), context.getSamuraiGuild().getScoreMap(), context.getSamuraiGuild().getUser(Long.parseLong(context.getAuthor().getUser().getId())));
        }
        return null;
    }
}
