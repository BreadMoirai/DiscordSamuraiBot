package samurai.command.debug;

import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
@Key("uptime")
public class Uptime extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        long timeDifference = System.currentTimeMillis() - Bot.START_TIME;
        int seconds = (int) ((timeDifference / 1000) % 60);
        int minutes = (int) ((timeDifference / 60000) % 60);
        int hours = (int) ((timeDifference / 3600000) % 24);
        int days = (int) (timeDifference / 86400000);
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(String.format("%d days, ", days));
        if (hours > 0) sb.append(String.format("%d hours, ", hours));
        if (minutes > 0) sb.append(String.format("%d minutes, ", minutes));
        sb.append(String.format("%d seconds.", seconds));
        return FixedMessage.build(sb.toString());
    }
}

