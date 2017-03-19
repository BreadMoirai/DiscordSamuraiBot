package samurai.command.primary;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.SamuraiGuild;
import samurai.entities.SamuraiUser;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("info")
public class Info extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<Member> mentions = context.getMentionsMembers();
        final SamuraiGuild guild = context.getGuild();
        Member userD;
        if (mentions.size() == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Info", null, null)
                    .setColor(context.getAuthor().getColor())
                    .setDescription(String.format("**Guild ID:** `%d`%n**Prefix:** `%s`%n**Linked Users:** `%d`%n**Score Count:** `%d`%n**Dedicated Channel:** `%d`", guild.getGuildId(), guild.getPrefix(), guild.getUserCount(), guild.getScoreCount(), guild.getDedicatedChannel()))
                    .setFooter("SamuraiStats™", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        } else userD = mentions.get(0);
        if (!guild.hasUser(Long.parseLong(userD.getUser().getId()))) {
            return FixedMessage.build(String.format("No info found for **%s**.", userD.getEffectiveName()));
        } else {
            SamuraiUser userS = guild.getUser(Long.parseLong(userD.getUser().getId()));
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(userD.getEffectiveName(), null, userD.getUser().getEffectiveAvatarUrl())
                    .setColor(userD.getColor())
                    .setTimestamp(OffsetDateTime.now())
                    .setDescription(String.format("**DiscordID: **%d%n**OsuID: **%d%n**Osu Name: **%s%n**Global Rank: **#%d%n**Country Rank: **#%d%n**Guild Rank: **#%d of %d%n", userS.getDiscordId(), userS.getOsuId(), userS.getOsuName(), userS.getG_rank(), userS.getC_rank(), userS.getL_rank(), guild.getUserCount()))
                    .setFooter("SamuraiStats™", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        }
    }
}

