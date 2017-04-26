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
package samurai.command.manage;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;

import java.util.List;

@Source
@Key("unlock")
public class Unlock extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        //add database entries to restrict channels
        final Guild guild = context.getGuild();
        List<TextChannel> hiddenChannels;
        if (context.getContent().equalsIgnoreCase("nsfw")) {
            hiddenChannels = guild.getTextChannelsByName("nsfw", true);
        } else if (context.getContent().equalsIgnoreCase("testing")) {
            hiddenChannels = guild.getTextChannelsByName("bottesting", true);
        } else return null;
        if (hiddenChannels.isEmpty()) return null;
        final TextChannel thatChannel = hiddenChannels.get(0);
        thatChannel.createPermissionOverride(context.getAuthor()).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
        return null;
    }
}
