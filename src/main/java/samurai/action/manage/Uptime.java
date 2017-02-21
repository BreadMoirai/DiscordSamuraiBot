package samurai.action.manage;

import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
@Key("uptime")
public class Uptime extends Action {
    @Override
    protected SamuraiMessage buildMessage() {
        long timeDifference = System.currentTimeMillis() - Bot.initializationTime;
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

