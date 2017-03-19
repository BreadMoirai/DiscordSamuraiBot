package samurai.command.restricted;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 3/16/2017
 */
@Key("purge")
@Source
@Creator
public class Purge extends Command{

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        context.getChannel().getHistory().retrievePast(100).queue(messages -> messages.forEach(message -> message.delete().queue()));
        return null;
    }
}
