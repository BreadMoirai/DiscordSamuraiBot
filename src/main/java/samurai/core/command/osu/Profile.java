package samurai.core.command.osu;

import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;
import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;
import samurai.osu.OsuJsonReader;
import samurai.osu.enums.Grade;

import java.awt.*;
import java.time.OffsetDateTime;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("profile")
public class Profile extends Command {

    @Override
    public SamuraiMessage buildMessage() {
        JSONObject profile;
        if (args.size() == 0 && mentions.size() == 0) {
            long authorId = Long.parseLong(author.getUser().getId());
            if (guild.hasUser(authorId)) {
                profile = OsuJsonReader.getUserJSON(String.valueOf(guild.getUser(authorId).getOsuId()));
                if (profile == null)
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", guild.getUser(authorId).getOsuName()));
            } else
                return FixedMessage.build(String.format("%s, you have not yet linked your osu profile. Try using `!link [Osu!Username]`", author.getAsMention()));
        } else if (args.size() == 1 && mentions.size() == 0) {
            profile = OsuJsonReader.getUserJSON(args.get(0));
            if (profile == null) {
                return FixedMessage.build(String.format("Could not find Osu!User **%s**", args.get(0)));
            }
        } else if (mentions.size() == 1) {
            long userId = Long.parseLong(mentions.get(0).getUser().getId());
            if (guild.hasUser(userId)) {
                profile = OsuJsonReader.getUserJSON(String.valueOf(guild.getUser(userId).getOsuId()));
                if (profile == null) {
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", guild.getUser(userId).getOsuName()));
                }
            } else
                return FixedMessage.build(String.format("**%1$s** has not yet linked their osu profile. Try using `!link @%1$s [Osu!Username]`", mentions.get(0).getEffectiveName()));
        } else {
            return FixedMessage.build("Please provide a valid argument.");
        }
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(profile.getString("username"), String.format("https://osu.ppy.sh/u/%s", profile.getString("username")))
                .setColor(Color.PINK)
                .setThumbnail("http://s.ppy.sh/a/" + profile.get("user_id"))
                .addField("Level", profile.getString("level"), true)
                .addField("Rank", String.format("**#**%s", profile.getString("pp_rank")), true)
                .addField("Play Count", profile.getString("playcount"), true)
                .addField("Accuracy", profile.getString("accuracy").substring(0, 5) + "%", true)
                .addField("Grades", String.format("%s%s\t%s%s\t%s%s", Grade.X.getEmote(), profile.getString("count_rank_ss"), Grade.S.getEmote(), profile.getString("count_rank_s"), Grade.A.getEmote(), profile.getString("count_rank_a")), true)
                .setFooter(profile.getString("user_id"), "http://w.ppy.sh/c/c9/Logo.png")
                .setTimestamp(OffsetDateTime.now());
        return FixedMessage.build(eb.build());
    }
}
