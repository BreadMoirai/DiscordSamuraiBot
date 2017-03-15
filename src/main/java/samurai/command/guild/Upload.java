package samurai.command.guild;

import net.dv8tion.jda.core.entities.Message;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.ConflictMerge;
import samurai.data.SamuraiStore;

import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("upload")
public class Upload extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        List<Message.Attachment> attaches = context.getAttaches();
        if (context.getAttaches().size() != 1 || !context.getAttaches().get(0).getFileName().endsWith(".db")) {
            return FixedMessage.build("❌ No valid attachment found.");
        } else if (context.getAttaches().get(0).getFileName().equalsIgnoreCase("scores.db")) {
            return new ConflictMerge(SamuraiStore.readScores(SamuraiStore.downloadFile(attaches.get(0))), context.getGuild().getScoreMap(), context.getGuild().getUser(Long.parseLong(context.getAuthor().getUser().getId())));
        }
        return null;
    }
}
