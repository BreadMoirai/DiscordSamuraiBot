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
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.enums.GameMode;
import samurai.osu.tracker.OsuTracker;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/7/2017
 */
@Key("tracking")
public class Tracking extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final SGuild sGuild = context.getSamuraiGuild();
        final Optional<Entry<Long, GameMode>> channelFilter = sGuild.getChannelFilters().stream().filter(longGameModeEntry -> longGameModeEntry.getValue() == GameMode.STANDARD).findAny();
        if (!channelFilter.isPresent()) {
            return FixedMessage.build("You do not have a channel set. try using `setchannel #myscores`");
        } else {
            final TextChannel outputChannel = context.getGuild().getTextChannelById(String.valueOf(channelFilter.get().getKey()));
            context.getGuild().getMembers().stream().filter(member -> member.getGame() != null && member.getGame().getName().equalsIgnoreCase("osu!")).map(value -> value.getUser().getId()).mapToLong(Long::parseLong).filter(value -> !OsuTracker.isTracking(value)).mapToObj(sGuild::getPlayer).filter(Optional::isPresent).map(Optional::get).forEach(player -> OsuTracker.register(player, outputChannel));
        }
        final String s = sGuild.getPlayers().stream().filter(player -> OsuTracker.isTracking(player.getDiscordId()))
                .map(player -> player.getDiscordId() + " -> " + player.getOsuId() + "|" + player.getOsuName()).collect(Collectors.joining("\n", "```\n", "\n```"));
        return FixedMessage.build(s);
    }
}
