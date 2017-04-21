package samurai.command.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/20/2017
 */
@Source
@Key("color")
public class ColorChange extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final java.util.List<Member> members = context.getMentionedMembers();
        if (members.size() == 1) {
            final Member member = members.get(0);
            final Color color = member.getColor();
            return FixedMessage.build(new EmbedBuilder().setColor(color).appendDescription(member.getEffectiveName() + "'s color is `0x" + Integer.toHexString(color.getRGB() & 0xFFFFFF).toUpperCase() + "`\n" + color.toString()).build());
        }
        final Member author = context.getAuthor();
        if (!context.hasContent()) {
            final Color color = author.getColor();
            return FixedMessage.build(new EmbedBuilder().setColor(color).appendDescription("Your color is `0x" + Integer.toHexString(color.getRGB() & 0xFFFFFF).toUpperCase() + "`\n" + color.toString()).build());
        }
        String strColor = context.getContent();
        Color newColor;
        try {
            Field field = Color.class.getField(strColor);
            newColor = (Color)field.get(null);
        } catch (Exception ignored) {
            newColor = null; // Not defined
        }
        if (newColor == null) {
            if (context.isHex()) {
                try {
                    if (strColor.startsWith("#")) {
                        strColor = "0x" + strColor.substring(1);
                    }
                    if (!strColor.startsWith("0x")) {
                        strColor = "0x" + strColor;
                    }
                    newColor = Color.decode(strColor);
                } catch (NumberFormatException ignored) {
                    ignored.printStackTrace();
                    return FixedMessage.build("Color not found");
                }
            } else return FixedMessage.build("Color not found");
        }
        final String colorHex = Integer.toHexString(newColor.getRGB() & 0xFFFFFF).toUpperCase();
        final String name = "Color: " + colorHex;
        final Guild guild = context.getGuild();
        final java.util.List<Role> existingColorRole = guild.getRolesByName(name, false);
        final List<Role> colorRolesToRemove = author.getRoles().stream().filter(role -> role.getName().startsWith("Color: ") && !role.getName().equals(name)).collect(Collectors.toList());
        if (!existingColorRole.isEmpty()) {
            guild.getController().modifyMemberRoles(author, existingColorRole, colorRolesToRemove).queue();
        } else {
            guild.getController().createRole().setName(name).setColor(newColor).queue(role -> {
                guild.getController().modifyMemberRoles(author, Collections.singletonList(role), colorRolesToRemove).queue();
                guild.getController().modifyRolePositions(false).selectPosition(role).moveTo(2).queue();
            });
        }
        colorRolesToRemove.stream().filter(role -> guild.getMembersWithRoles(role).isEmpty()).forEach(role -> role.delete().queue());
        return FixedMessage.build("Your color has been successfully set to `Ox" + colorHex + "`");
    }
}
