package samurai.action.admin;

import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Admin;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

/**
 * @author TonTL
 * @version 4.x - 2/18/2017
 */
@Key("perm")
@Admin
@Client
public class PermissionGet extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        StringBuilder s = new StringBuilder().append("```\n");
        for (net.dv8tion.jda.core.Permission p : client.getGuildById(String.valueOf(guildId)).getMember(Bot.self).getPermissions(client.getTextChannelById(String.valueOf(channelId))))
            s.append(p.toString()).append("\n");
        return FixedMessage.createSimple(s.append("```").toString());
    }
}
