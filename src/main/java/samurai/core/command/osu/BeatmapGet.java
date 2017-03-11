package samurai.core.command.osu;

import samurai.core.command.Command;
import samurai.core.command.annotations.Guild;
import samurai.core.command.annotations.Key;
import samurai.core.entities.SamuraiMessage;
import samurai.core.entities.dynamic.RandomBeatmapDisplay;

/**
 * @author TonTL
 * @version 4.x - 2/25/2017
 */
@Key("beatmap")
@Guild
public class BeatmapGet extends Command {

    @Override
    protected SamuraiMessage buildMessage() {
        return new RandomBeatmapDisplay(guild.getScoreMap());
    }
}
