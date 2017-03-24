package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.data.SamuraiDatabase;
import samurai.entities.SamuraiUser;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.dynamic.StaticBeatmapDisplay;
import samurai.osu.entities.Score;
import samurai.osu.enums.GameMode;
import samurai.util.OsuAPI;

import java.util.Optional;

/**
 * @author TonTL
 * @version 3/21/2017
 */
@Key("recent")
@Source
public class GetRecent extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final SamuraiUser user = context.getGuild().getUser(Long.parseLong(context.getAuthor().getUser().getId()));
        if (user == null) {
            return FixedMessage.build("Please link your osu! profile.");
        }
        final Optional<Score> userRecent = OsuAPI.getUserRecent(user.getOsuName(), user.getOsuId(), GameMode.OSU, 50).stream().filter(Score::passed).findFirst();
        if (!userRecent.isPresent()) return FixedMessage.build("Nothing found.");
        final Score lastScore = userRecent.get();
        return new StaticBeatmapDisplay(SamuraiDatabase.getSet(lastScore.getBeatmapHash()), true, true, lastScore.getBeatmapHash(), lastScore);
    }
}
