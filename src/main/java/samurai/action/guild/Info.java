package samurai.action.guild;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.data.SamuraiUser;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.awt.*;
import java.time.OffsetDateTime;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("info")
@Guild
public class Info extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        Color color = author.getColor();
        User userD;
        if (mentions.size() == 0)
            userD = author.getUser();
        else userD = mentions.get(0);
        if (!guild.hasUser(Long.parseLong(userD.getId()))) {
            return FixedMessage.build(String.format("No info found for **%s**.", userD.getName()));
        } else {
            SamuraiUser userS = guild.getUser(Long.parseLong(userD.getId()));
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(userD.getName(), null, userD.getAvatarUrl())
                    .setColor(color)
                    .setTimestamp(OffsetDateTime.now())
                    .setDescription(String.format("**DiscordID: **%d%n**OsuID: **%d%n**Osu Name: **%s%n**Global Rank: **#%d%n**Country Rank: **#%d%n**Guild Rank: **#%d of %d%n", userS.getDiscordId(), userS.getOsuId(), userS.getOsuName(), userS.getG_rank(), userS.getC_rank(), userS.getL_rank(), guild.getUserCount()))
                    .setFooter("SamuraiStats™", Bot.AVATAR);
            return FixedMessage.build(eb.build());
        }
    }
}

