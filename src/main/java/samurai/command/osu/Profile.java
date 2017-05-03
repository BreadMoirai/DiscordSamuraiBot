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
import net.dv8tion.jda.core.entities.MessageEmbed;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.database.dao.PlayerDao;
import samurai.database.objects.Player;
import samurai.database.objects.PlayerBuilder;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.OsuAPI;
import samurai.osu.enums.Grade;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@Key("profile")
public class Profile extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        PlayerBuilder player;
        java.util.List<Member> mentions = context.getMentionedMembers();
        Member author = context.getAuthor();
        List<String> args = context.getArgs();
        if (context.getArgs().size() == 0 && mentions.size() == 0) {
            final Player playerBean = Database.get().<PlayerDao, Player>openDao(PlayerDao.class, playerDao -> playerDao.getPlayer(author.getUser().getIdLong()));
            if (playerBean != null) {
                player = OsuAPI.getPlayer(playerBean.getOsuId());
                if (player == null)
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", playerBean.getOsuName()));
                player.setDiscordId(author.getUser().getIdLong());
            } else
                return FixedMessage.build(String.format("%s, you have not yet linked your osu profile. Try using `!link [Osu!Username]`", author.getAsMention()));
        } else if (args.size() == 1 && mentions.size() == 0) {
            player = OsuAPI.getPlayer(args.get(0));
            if (player == null) {
                return FixedMessage.build(String.format("Could not find Osu!User **%s**", args.get(0)));
            }
        } else if (mentions.size() == 1) {
            final Player playerBean = Database.get().<PlayerDao, Player>openDao(PlayerDao.class, playerDao -> playerDao.getPlayer(mentions.get(0).getUser().getIdLong()));
            if (playerBean != null) {
                player = OsuAPI.getPlayer(playerBean.getOsuId());
                if (player == null) {
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", playerBean.getOsuName()));
                }
                player.setDiscordId(mentions.get(0).getUser().getIdLong());
            } else
                return FixedMessage.build(String.format("**%1$s** has not yet linked their osu profile. Try using `!link @%1$s [Osu!Username]`", mentions.get(0).getEffectiveName()));
        } else {
            return FixedMessage.build("Please provide a valid argument.");
        }
        return FixedMessage.build(buildProfileEmbed(player.build()));
    }

    static MessageEmbed buildProfileEmbed(Player player) {
        final String accuracy = String.valueOf(player.getAccuracy());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(player.getOsuName(), String.format("https://osu.ppy.sh/u/%s", player.getOsuName()))
                .setColor(Color.PINK)
                .setThumbnail("http://s.ppy.sh/a/" + player.getOsuId())
                .addField("Level", String.valueOf(player.getLevel()), true)
                .addField("Rank", String.format("**#**%s", player.getGlobalRank()), true)
                .addField("Play Count", String.valueOf(player.getPlayCount()), true)
                .addField("Accuracy", accuracy.substring(0, accuracy.length() < 5 ? accuracy.length() : 5) + '%', true)
                .addField("Grades", String.format("%s%s\t%s%s\t%s%s", Grade.X.getEmote(), player.getCountX(), Grade.S.getEmote(), player.getCountS(), Grade.A.getEmote(), player.getCountA()), true)
                .addField("HitCount", String.format("%s: `%s`\t%s: `%s`\t%s: `%s`", "300", player.getCount300(), "100", player.getCount100(), "50", player.getCount50()), true)
                .setFooter(String.valueOf(player.getOsuId()), "http://w.ppy.sh/c/c9/Logo.png")
                .setTimestamp(Instant.ofEpochSecond(player.getLastUpdated()));
        return eb.build();
    }
}
