package samurai.command.restricted;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
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
public class Purge extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final TextChannel channel = context.getChannel();
        final MessageHistory history = channel.getHistory();
        if (context.isInt()) {
            int i = Integer.parseInt(context.getContent());
            while (i > 100) {
                history.retrievePast(100).queue(messages -> channel.deleteMessages(messages).queue());
                i -= 100;
            }
            history.retrievePast(i).queue(messages -> channel.deleteMessages(messages).queue());
        } else {
            history.retrievePast(100).queue(messages -> {
                if (messages.size() < 3)
                    messages.stream().mapToLong(Message::getIdLong).mapToObj(channel::deleteMessageById).forEach(RestAction::queue);
                else
                    channel.deleteMessages(messages).queue();
            });
        }
        return null;
    }
}
