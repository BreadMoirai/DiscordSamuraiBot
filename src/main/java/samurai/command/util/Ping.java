package samurai.command.util;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

import static java.time.temporal.ChronoUnit.MILLIS;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
@Key("ping")
public class Ping extends Command {

    public SamuraiMessage execute(CommandContext context) {
        return FixedMessage.build("Calculating Ping...").setConsumer(message -> message.editMessage(String.format("Pung! %dms", MILLIS.between(context.getTime(), message.getCreationTime()))).queue());
    }
}