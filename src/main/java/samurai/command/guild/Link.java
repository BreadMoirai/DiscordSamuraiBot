package samurai.command.guild;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.json.JSONObject;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.osu.Profile;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.util.OsuAPI;

import java.util.Collections;
import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/20/2017
 */
@Key("link")
public class Link extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (context.getArgs().size() == 0 || context.getArgs().get(0).length() > 16) {
            return FixedMessage.build("Invalid Username");
        }
        JSONObject userJSON = OsuAPI.getUserJSON(context.getArgs().get(0));
        if (userJSON == null) {
            return FixedMessage.build("Failed to link account.");
        }
        Message profileMessage;
        List<Member> mentions = context.getMentions();
        if (context.getMentions().size() == 0) {
            context.getGuild().addUser(Long.parseLong(context.getAuthor().getUser().getId()), userJSON);
            profileMessage = ((FixedMessage) new Profile().execute(new CommandContext("", context.getAuthor(), Collections.emptyList(), "", Collections.emptyList(), context.getGuildId(), context.getChannelId(), 0L, null, null))).getMessage();
        } else if (context.getMentions().size() == 1) {
            if (!PermissionUtil.canInteract(context.getAuthor(), context.getMentions().get(0)))
                return FixedMessage.build("You do not have sufficient access to manage " + context.getMentions().get(0).getAsMention());
            context.getGuild().addUser(Long.parseLong(mentions.get(0).getUser().getId()), userJSON);
            profileMessage = ((FixedMessage) new Profile().execute(new CommandContext("", mentions.get(0), Collections.emptyList(), "", Collections.emptyList(), context.getGuildId(), context.getChannelId(), 0L,null , null))).getMessage();
        } else {
            return FixedMessage.build("Failed to link account.");
        }
        if (profileMessage.getEmbeds().size() != 1) {
            return FixedMessage.build(profileMessage.getContent().replaceAll("<@.*>", profileMessage.getMentionedUsers().get(0).getAsMention()));
        }
        return new FixedMessage().setMessage(new MessageBuilder()
                .append("Successfully linked **")
                .append(mentions.size() == 1 ? mentions.get(0).getAsMention() : context.getAuthor().getAsMention())
                .append("** to osu account")
                .setEmbed(profileMessage.getEmbeds().get(0))
                .build());


    }
}
