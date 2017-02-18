package samurai.action.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.annotations.ActionKeySet;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.data.SamuraiFile;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

import java.util.List;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("help")
@Guild
@ActionKeySet
public class Help extends Action {

    /**
     * @return A Message with an Embed created using resources/help.txt
     */
    @Override
    public SamuraiMessage buildMessage() {
        if (args.size() != 1) {
            MessageBuilder messageBuilder = new MessageBuilder();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("Samurai - help.txt", null, AVATER_URL);
            String token = guild.getPrefix();
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
        } else if (actionKeySet.contains(args.get(0)))
            return FixedMessage.createSimple(SamuraiFile.getHelp(args.get(0)).replace("[prefix]", guild.getPrefix()));
        else return FixedMessage.createSimple("Yeah I don't think that's a real command.");
    }
}
