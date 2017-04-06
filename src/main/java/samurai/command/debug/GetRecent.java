package samurai.command.debug;

import samurai.Database;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.model.Player;
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
        final Optional<Player> playerOptional = Database.getDatabase().getPlayer(context.getAuthorId());
        if (!playerOptional.isPresent()) {
            return FixedMessage.build("Please link your osu! profile.");
        }
        final Player player = playerOptional.get();
        final Optional<Score> userRecent = OsuAPI.getUserRecent(player.getOsuName(), player.getOsuId(), GameMode.OSU, 50).stream().filter(Score::passed).findFirst();
        if (!userRecent.isPresent()) return FixedMessage.build("Nothing found.");
        final Score lastScore = userRecent.get();
        //return new StaticBeatmapDisplay(SamuraiDatabase.getSet(lastScore.getBeatmapHash()), true, true, lastScore.getBeatmapHash(), lastScore);
        //todo
        return FixedMessage.build("Unsupported Operation");
    }
}
