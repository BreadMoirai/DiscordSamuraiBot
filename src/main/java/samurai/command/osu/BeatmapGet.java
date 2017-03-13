package samurai.command.osu;

import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.RandomBeatmapDisplay;

/**
 * @author TonTL
 * @version 4.x - 2/25/2017
 */
@Key("beatmap")
public class BeatmapGet extends Command {

    @Override
    protected SamuraiMessage buildMessage() {
        return new RandomBeatmapDisplay(guild.getScoreMap());
    }
}
