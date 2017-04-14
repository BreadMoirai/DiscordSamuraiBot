package samurai.command.osu;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.x - 2/25/2017
 */
@Key("beatmap")
public class RandomBeatmap extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        //fixme
        return null;
    }
}
