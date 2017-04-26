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

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.entities.model.SGuild;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.enums.GameMode;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/17/2017
 */
@Key("setchannel")
public class SetChannel extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().size() != 1) {
            return null;
        }
        TextChannel targetChannel;
        if (context.getMentionedChannels().size() == 1) {
            targetChannel = context.getMentionedChannels().get(0);
        } else {
            targetChannel = context.getChannel();
        }
        final GameMode gameMode = GameMode.find(context.getStrippedContent());
        final Optional<SGuild> guildOptional = Database.getDatabase().getGuild(context.getGuildId());
        if (guildOptional.isPresent()) {
            final SGuild sGuild = guildOptional.get();
            if (gameMode != null) {
                sGuild.getManager().addChannelFilter(targetChannel.getIdLong(), gameMode);
                return FixedMessage.build("Tracking notifications will be sent to <#" + targetChannel.getId() + "> for `" + gameMode.toString() + "`");
            }
            else {
                sGuild.getManager().removeChannelFilter(targetChannel.getIdLong());
                return FixedMessage.build("<#" + targetChannel.getId() + "> will no longer receive tracking notifications");
            }
        }
        return null;
    }
}
