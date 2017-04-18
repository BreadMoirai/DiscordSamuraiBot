package samurai.command.debug;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("info")
public class Info extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<Member> mentions = context.getMentionedMembers();
        final SGuild team = context.getSamuraiGuild();
        Member userD;
        if (mentions.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Info", null, null)
                    .setColor(context.getAuthor().getColor())
                    .setDescription(String.format("**Guild ID:** `%d`%n**Prefix:** `%s`%n**Linked Users:** `%d`%n**Dedicated Channels:** `%s`", team.getGuildId(), team.getPrefix(), team.getPlayerCount(), team.getChannelFilters().toString()))
                    .setFooter("SamuraiStats™", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        } else userD = mentions.get(0);
        final Optional<Player> guildPlayer = team.getPlayer(Long.parseLong(userD.getUser().getId()));
        if (!guildPlayer.isPresent()) {
            return FixedMessage.build(String.format("No info found for **%s**.", userD.getEffectiveName()));
        } else {
            Player player = guildPlayer.get();
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(userD.getEffectiveName(), null, userD.getUser().getEffectiveAvatarUrl())
                    .setColor(userD.getColor())
                    .setTimestamp(OffsetDateTime.now())
                    .setDescription(String.format("**DiscordID: **%d%n**OsuID: **%d%n**Osu Name: **%s%n**Global Rank: **#%d%n**Country Rank: **#%d%n**Guild Rank: **#%d of %d%n**Last Updated: **%.2f days ago.", player.getDiscordId(), player.getOsuId(), player.getOsuName(), player.getRankG(), player.getRankC(), team.getRankL(player), team.getPlayerCount(), Instant.ofEpochSecond(player.getLastUpdated()).until(Instant.now(), ChronoUnit.HOURS)/24.00))
                    .setFooter("SamuraiStats™", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        }
    }
}

