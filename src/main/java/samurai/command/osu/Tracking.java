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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.tuple.Pair;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.database.objects.GuildBean;
import samurai.database.objects.PlayerBean;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.enums.GameMode;
import samurai.osu.tracker.OsuTracker;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Key("tracking")
public class Tracking extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final GuildBean samuraiGuild = context.getSamuraiGuild();
        final List<PlayerBean> players = samuraiGuild.getPlayers();
        final List<Pair<Long, Short>> channelModes = samuraiGuild.getChannelModes();
        final Guild guild = context.getGuild();
        if (channelModes.isEmpty()) {
            return FixedMessage.build("You do not have a channel set. try using `setchannel standard`");
        } else {
            StringBuilder stringBuilder = new StringBuilder().append("```glsl");
            for (Pair<Long, Short> channelMode : channelModes) {
                final TextChannel textChannel = guild.getTextChannelById(channelMode.getKey());
                if (textChannel != null) {
                    final List<GameMode> modes = GameMode.getModes(channelMode.getValue());
                    stringBuilder.append("\n#").append(textChannel.getName()).append(" - ").append(Arrays.toString(modes.toArray()));
                    for (PlayerBean player : players) {
                        if (modes.stream().anyMatch(gameMode -> gameMode.tracks(player))) {
                            stringBuilder.append("\n").append(guild.getMemberById(player.getDiscordId()).getEffectiveName()).append(" - ").append(player.getOsuName());
                        }
                    }
                }
            }
            return (FixedMessage.build(stringBuilder.append("\n```").toString()));
        }
    }
}
