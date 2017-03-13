package samurai.command.guild;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.json.JSONObject;
import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.command.osu.Profile;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.util.OsuAPI;

import java.util.Collections;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("link")
public class Link extends Command {


    @Override
    protected SamuraiMessage buildMessage() {
        if (args.size() == 0 || args.get(0).length() > 16) {
            return FixedMessage.build("Invalid Username");
        }
        JSONObject userJSON = OsuAPI.getUserJSON(args.get(0));
        if (userJSON == null) {
            return FixedMessage.build("Failed to link account.");
        }
        Message profileMessage;
        if (mentions.size() == 0) {
            guild.addUser(Long.parseLong(author.getUser().getId()), userJSON);
            profileMessage = ((FixedMessage) new Profile()
                    .setAuthor(author)
                    .setMentions(Collections.emptyList())
                    .setArgs(Collections.emptyList())
                    .setGuild(guild)
                    .setChannelId(channelId)
                    .call()
                    .orElse(FixedMessage.build("Error")))
                    .getMessage();
        } else if (mentions.size() == 1) {
            if (!PermissionUtil.canInteract(author, mentions.get(0)))
                return FixedMessage.build("You do not have sufficient access to manage " + mentions.get(0).getAsMention());
            guild.addUser(Long.parseLong(mentions.get(0).getUser().getId()), userJSON);
            profileMessage = ((FixedMessage) new Profile()
                    .setMentions(mentions)
                    .setArgs(Collections.emptyList())
                    .setGuild(guild)
                    .setChannelId(channelId)
                    .call()
                    .orElse(FixedMessage.build("Error")))
                    .getMessage();
        } else {
            return FixedMessage.build("Failed to link account.");
        }
        if (profileMessage.getEmbeds().size() != 1) {
            return FixedMessage.build(profileMessage.getContent().replaceAll("<@.*>", profileMessage.getMentionedUsers().get(0).getAsMention()));
        }
        return new FixedMessage().setMessage(new MessageBuilder()
                .append("Successfully linked **")
                .append(mentions.size() == 1 ? mentions.get(0).getAsMention() : author.getAsMention())
                .append("** to osu account")
                .setEmbed(profileMessage.getEmbeds().get(0))
                .build());


    }
}
