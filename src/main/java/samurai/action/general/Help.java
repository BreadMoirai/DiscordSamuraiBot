package samurai.action.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.action.Action;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.data.Store;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("help")
@Guild
public class Help extends Action {

    /**
     * @return A Message with an Embed created using resources/help.txt
     */
    @Override
    public SamuraiMessage buildMessage() {
        if (args.size() != 1) {
            EmbedBuilder embedBuilder;
            embedBuilder = new EmbedBuilder()
                    .setAuthor("Samurai - help.txt", null, AVATAR)
                    .setDescription(Store.getHelp("cmdlist").replace("[prefix]", guild.getPrefix()));
            return new FixedMessage()
                    .setMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build());
        } else {
            return FixedMessage.build(Store.getHelp(args.get(0)).replace("[prefix]", guild.getPrefix()));
        }
        //return FixedMessage.build("Yeah I don't think that's a real command.");
    }
}
