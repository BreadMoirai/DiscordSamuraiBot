package samurai.command.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.data.SamuraiStore;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;

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
    public SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().size() != 1) {
            EmbedBuilder embedBuilder;
            embedBuilder = new EmbedBuilder()
                    .setAuthor("Samurai - help.txt", null, Bot.AVATAR)
                    .setDescription(SamuraiStore.getHelp("cmdlist").replace("[prefix]", context.getGuild().getPrefix()));
            return new FixedMessage()
                    .setMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build());
        } else {
            return FixedMessage.build(SamuraiStore.getHelp(context.getArgs().get(0)).replace("[prefix]", context.getGuild().getPrefix()));
        }
        //return FixedMessage.build("Yeah I don't think that's a real command.");
    }
}
