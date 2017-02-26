package samurai.action.guild;

import samurai.action.Action;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.data.SamuraiStore;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.ConflictMerge;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("upload")
@Guild
public class Upload extends Action {
    @Override
    protected SamuraiMessage buildMessage() {
        if (attaches.size() != 1 || !attaches.get(0).getFileName().endsWith(".db")) {
            return FixedMessage.build("❌ No valid attachment found.");
        } else if (attaches.get(0).getFileName().equalsIgnoreCase("scores.db")) {
            return new ConflictMerge(SamuraiStore.readScores(SamuraiStore.downloadFile(attaches.get(0))), guild.getScoreMap(), guild.getUser(Long.parseLong(author.getUser().getId())));
        }
        return null;
    }
}
