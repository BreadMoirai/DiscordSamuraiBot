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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.osu.Profile;
import samurai.database.objects.GuildBean;
import samurai.database.objects.PlayerBean;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Key("info")
public class Info extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<Member> mentions = context.getMentionedMembers();
        final GuildBean guild = context.getSamuraiGuild();
        Member userD;
        if (mentions.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Info", null, null)
                    .setColor(context.getAuthor().getColor())
                    .setDescription(String.format("**Guild ID:** `%d`%n**Prefix:** `%s`%n**Linked Users:** `%d`%n**Tracker Channels:** `%s`", guild.getGuildId(), guild.getPrefix(), guild.getPlayers().size(), guild.getChannelModes().toString()))
                    .setFooter("SamuraiStats\u2122", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        } else userD = mentions.get(0);
        final Optional<PlayerBean> guildPlayer = context.getSamuraiGuild().getPlayer(context.getAuthorId());
        if (!guildPlayer.isPresent()) {
            return FixedMessage.build(String.format("No info found for **%s**.", userD.getEffectiveName()));
        } else {
            PlayerBean player = guildPlayer.get();
            return FixedMessage.build(Profile.buildProfileEmbed(player));
        }
    }
}

