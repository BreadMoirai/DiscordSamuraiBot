package samurai.action.generic;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.action.Action;
import samurai.data.SamuraiFile;

import java.util.List;

/**
 * Gets a help embed created from help.txt
 * Created by TonTL on 2/12/2017.
 */
public class HelpAction extends Action {

    @Override
    public Message call() {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Samurai - help.txt", null, AVATER_URL);
        String token = guildId == null ? "!" : SamuraiFile.getPrefix(guildId);
        List<String> textFile = SamuraiFile.readTextFile("help.txt");
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : textFile) {
            if (token != null)
                stringBuilder.append(line.replace("[prefix]", token)).append("\n");
            else
                stringBuilder.append(line).append("\n");
        }
        embedBuilder.setDescription(stringBuilder.toString());
        return messageBuilder.setEmbed(embedBuilder.build()).build();
    }

}
