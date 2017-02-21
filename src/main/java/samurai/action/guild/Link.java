package samurai.action.guild;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONObject;
import samurai.action.Action;
import samurai.action.osu.Profile;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;
import samurai.osu.OsuJsonReader;

import java.util.Collections;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("link")
@Guild
public class Link extends Action {


    @Override
    protected SamuraiMessage buildMessage() {

        if (args.size() == 0 || args.get(0).length() > 16) {
            return FixedMessage.build("Invalid Username");
        }
        JSONObject userJSON = OsuJsonReader.getUserJSON(arg);
        if (userJSON == null) {
            return FixedMessage.build("Failed to link account.");
        }
        guild.addUser(Long.parseLong(author.getUser().getId()), userJSON);
        Message profileMessage = new Profile()
                .setAuthor(author)
                .setMentions(Collections.emptyList())
                .setArgs(Collections.emptyList())
                .setGuild(guild)
                .setChannelId(channelId)
                .call().orElse(FixedMessage.build("Error"))
                .getMessage();
        if (profileMessage.getEmbeds().size() != 1) {
            return FixedMessage.build(profileMessage.getContent().replaceAll("<@.*>", profileMessage.getMentionedUsers().get(0).getAsMention()));
        }
        return new FixedMessage().setMessage(new MessageBuilder()
                .append("Successfully linked **")
                .append(author.getEffectiveName())
                .append("** to osu account")
                .setEmbed(profileMessage.getEmbeds().get(0))
                .build());


    }
}
