package samurai.action.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.ActionKeySet;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.data.SamuraiStore;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

import java.net.URISyntaxException;

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
            EmbedBuilder embedBuilder;
            try {
                embedBuilder = new EmbedBuilder()
                        .setAuthor("Samurai - help.txt", null, AVATER_URL)
                        .setDescription(SamuraiStore.getHelp("cmdlist").replace("[prefix]", guild.getPrefix()));
            } catch (URISyntaxException e) {
                Bot.logError(e);
                return null;
            }
            return new FixedMessage()
                    .setMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build());
        } else if (actionKeySet.contains(args.get(0)))
            try {
                return FixedMessage.build(SamuraiStore.getHelp(args.get(0)).replace("[prefix]", guild.getPrefix()));
            } catch (URISyntaxException e) {
                Bot.logError(e);
                return null;
            }
        else return FixedMessage.build("Yeah I don't think that's a real command.");
    }
}
