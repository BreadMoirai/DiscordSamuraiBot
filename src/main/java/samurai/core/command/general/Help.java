package samurai.core.command.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.core.command.Command;
import samurai.core.command.annotations.Key;
import samurai.core.entities.base.FixedMessage;
import samurai.core.entities.base.SamuraiMessage;
import samurai.data.SamuraiStore;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("help")
public class Help extends Command {

    /**
     * @return A Message with an Embed created using resources/help.txt
     */
    @Override
    public SamuraiMessage buildMessage() {
        if (args.size() != 1) {
            EmbedBuilder embedBuilder;
            embedBuilder = new EmbedBuilder()
                    .setAuthor("Samurai - help.txt", null, AVATAR)
                    .setDescription(SamuraiStore.getHelp("cmdlist").replace("[prefix]", guild.getPrefix()));
            return new FixedMessage()
                    .setMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build());
        } else {
            return FixedMessage.build(SamuraiStore.getHelp(args.get(0)).replace("[prefix]", guild.getPrefix()));
        }
        //return FixedMessage.build("Yeah I don't think that's a real command.");
    }
}
