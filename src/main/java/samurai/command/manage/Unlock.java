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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Source
@Key("unlock")
public class Unlock extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        //add database entries to restrict channels
        final Guild guild = context.getGuild();
        final Member author = context.getAuthor();
        if (!context.hasContent()) {
            final String collect = guild.getTextChannels().stream().map(textChannel -> {
                if (author.hasPermission(textChannel, Permission.MESSAGE_READ)) {
                    return "+ " + textChannel.getName();
                } else {
                    return "- " + textChannel.getName();
                }
            }).sorted(Comparator.comparingInt(o -> o.codePointAt(0))).collect(Collectors.joining("\n", "```diff\n", "\n```"));
            return FixedMessage.build(collect);
        }
        List<TextChannel> hiddenChannels = guild.getTextChannelsByName(context.getContent(), true);
        if (hiddenChannels.isEmpty()) return FixedMessage.build("Channel not found");
        final TextChannel thatChannel = hiddenChannels.get(0);
        final PermissionOverride permissionOverride = thatChannel.getPermissionOverride(author);
        if (permissionOverride == null) {
            thatChannel.createPermissionOverride(author).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY).queue();
            return FixedMessage.build("Access granted");
        } else {
            if (permissionOverride.getAllowed().contains(Permission.MESSAGE_READ))
                return FixedMessage.build("Access has already been granted.");
            else return FixedMessage.build("Permission Override Denied");
        }
    }
}
