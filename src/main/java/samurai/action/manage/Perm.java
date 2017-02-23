package samurai.action.manage;

import net.dv8tion.jda.core.Permission;
import samurai.Bot;
import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TonTL
 * @version 4.x - 2/18/2017
 */
@Key("perm")
@Client
public class Perm extends Action {

    private static final List<Permission> required = Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY);

    @Override
    protected SamuraiMessage buildMessage() {
        List<Permission> permsNotFound = new LinkedList<>();
        List<Permission> permsFound = client.getGuildById(String.valueOf(guildId)).getMember(Bot.self).getPermissions(client.getTextChannelById(String.valueOf(channelId)));
        for (Permission p : required)
            if (!permsFound.contains(p))
                permsNotFound.add(p);
        StringBuilder sb = new StringBuilder().append("```diff\n");
        for (Permission p : required)
            if (permsFound.contains(p))
                sb.append("+ ").append(p).append("\n");
        sb.append("--- ").append("\n");
        for (Permission p : permsNotFound)
            sb.append("- ").append(p).append("\n");
        sb.append("--- ").append("\n");
        for (Permission p : permsFound)
            if (!required.contains(p))
                sb.append("~ ").append(p).append("\n");
        return FixedMessage.build(sb.append("```").toString());
    }
}
