package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.database.DatabaseSingleton;
import samurai.entities.model.Player;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.api.OsuAPI;
import samurai.osu.enums.GameMode;
import samurai.osu.model.Score;

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
        final Optional<Player> playerOptional = DatabaseSingleton.getDatabase().getPlayer(context.getAuthorId());
        if (!playerOptional.isPresent()) {
            return FixedMessage.build("Please link your osu! profile.");
        }
        final Player player = playerOptional.get();
        final Optional<Score> userRecent = OsuAPI.getUserRecent(player.getOsuName(), player.getOsuId(), GameMode.STANDARD, 50).stream().filter(Score::passed).findFirst();
        if (!userRecent.isPresent()) return FixedMessage.build("Nothing found.");
        final Score lastScore = userRecent.get();
        //return new StaticBeatmapDisplay(SamuraiDatabase.getSet(lastScore.getBeatmapHash()), true, true, lastScore.getBeatmapHash(), lastScore);
        //fixme
        return FixedMessage.build("Unsupported Operation");
    }
}
