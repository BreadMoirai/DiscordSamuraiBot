package samurai.command.debug;

import net.dv8tion.jda.core.Permission;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TonTL
 * @version 4.x - 2/18/2017
 */
@Key("perm")
public class Perm extends Command {

    private static final List<Permission> required = Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY);

    @Override
    public SamuraiMessage execute(CommandContext context) {
        List<Permission> permsNotFound = new LinkedList<>();
        List<Permission> permsFound = context.getGuild().getSelfMember().getPermissions(context.getChannel());
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
