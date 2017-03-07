package samurai.action.osu;

import samurai.action.Action;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.RandomBeatmapDisplay;

/**
 * @author TonTL
 * @version 4.x - 2/25/2017
 */
@Key("beatmap")
@Guild
public class BeatmapGet extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        return new RandomBeatmapDisplay(guild.getScoreMap());
    }
}
