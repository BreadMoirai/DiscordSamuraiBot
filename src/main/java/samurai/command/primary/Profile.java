package samurai.command.primary;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.JSONObject;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.OsuAPI;
import samurai.osu.enums.Grade;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("profile")
public class Profile extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        JSONObject profile;
        java.util.List<Member> mentions = context.getMentionedMembers();
        Member author = context.getAuthor();
        SGuild guild = context.getSGuild();
        List<String> args = context.getArgs();
        if (context.getArgs().size() == 0 && mentions.size() == 0) {
            long authorId = Long.parseLong(author.getUser().getId());
            final Optional<Player> playerOptional = guild.getPlayer(authorId);
            if (playerOptional.isPresent()) {
                final Player player = playerOptional.get();
                profile = OsuAPI.getUserJSON(String.valueOf(playerOptional.get().getOsuId()));
                if (profile == null)
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", player.getOsuName()));
            } else
                return FixedMessage.build(String.format("%s, you have not yet linked your osu profile. Try using `!link [Osu!Username]`", author.getAsMention()));
        } else if (args.size() == 1 && mentions.size() == 0) {
            profile = OsuAPI.getUserJSON(args.get(0));
            if (profile == null) {
                return FixedMessage.build(String.format("Could not find Osu!User **%s**", args.get(0)));
            }
        } else if (mentions.size() == 1) {
            long userId = Long.parseLong(mentions.get(0).getUser().getId());
            final Optional<Player> playerOptional = guild.getPlayer(userId);
            if (playerOptional.isPresent()) {
                final Player player = playerOptional.get();
                profile = OsuAPI.getUserJSON(String.valueOf(player.getOsuId()));
                if (profile == null) {
                    return FixedMessage.build(String.format("Could not find Osu!User **%s**", player.getOsuName()));
                }
            } else
                return FixedMessage.build(String.format("**%1$s** has not yet linked their osu profile. Try using `!link @%1$s [Osu!Username]`", mentions.get(0).getEffectiveName()));
        } else {
            return FixedMessage.build("Please provide a valid argument.");
        }
        return FixedMessage.build(buildProfileEmbed(profile));
    }

    static MessageEmbed buildProfileEmbed(JSONObject profile) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(profile.getString("username"), String.format("https://osu.ppy.sh/u/%s", profile.getString("username")))
                .setColor(Color.PINK)
                .setThumbnail("http://s.ppy.sh/a/" + profile.getString("user_id"))
                .addField("Level", profile.getString("level"), true)
                .addField("Rank", String.format("**#**%s", profile.getString("pp_rank")), true)
                .addField("Play Count", profile.getString("playcount"), true)
                .addField("Accuracy", profile.getString("accuracy").substring(0, 5) + '%', true)
                .addField("Grades", String.format("%s%s\t%s%s\t%s%s", Grade.X.getEmote(), profile.getString("count_rank_ss"), Grade.S.getEmote(), profile.getString("count_rank_s"), Grade.A.getEmote(), profile.getString("count_rank_a")), true)
                .setFooter(profile.getString("user_id"), "http://w.ppy.sh/c/c9/Logo.png")
                .setTimestamp(OffsetDateTime.now());
        return eb.build();
    }
}
