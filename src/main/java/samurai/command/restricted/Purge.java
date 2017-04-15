package samurai.command.restricted;

import net.dv8tion.jda.core.entities.TextChannel;
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
        final TextChannel channel = context.getChannel();
        channel.getHistory().retrievePast(100).queue(messages -> channel.deleteMessages(messages).queue());
        return null;
    }
}
