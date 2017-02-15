package samurai.action.generic;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.data.SamuraiFile;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.util.List;

/**
 * The type Help action.
 */
public class HelpAction extends Action {

    /**
     null     * @return A Message with an Embed created using resources/help.txt
     */
    @Override
    public SamuraiMessage buildMessage() {

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
        return new FixedMessage()
                .setMessage(messageBuilder.setEmbed(embedBuilder.build()).build());
    }

}
