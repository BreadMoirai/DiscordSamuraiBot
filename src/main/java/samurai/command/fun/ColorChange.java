package samurai.command.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
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
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            sb.append(member.getEffectiveName())
                    .append("'s color is `")
                    .append(colorHex(color))
                    .append("`\n")
                    .append(color.toString());
            eb.setColor(color);
            return FixedMessage.build(eb.build());
        }
        final Member author = context.getAuthor();
        if (!context.hasContent()) {
            final Color color = author.getColor();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            sb.append("Your color is `")
                    .append(colorHex(color))
                    .append("`\n")
                    .append(color.toString());
            eb.setColor(color);
            return FixedMessage.build(eb.build());
        }
        String strColor = context.getContent();
        Color newColor;
        try {
            Field field = Color.class.getField(strColor);
            newColor = (Color) field.get(null);
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
        if ((newColor.getRGB() & 0xFFFFFF) == 0) {
            newColor = new Color(0, 0, 1);
        }

        final String colorHex = colorHex(newColor);
        final String name = "Color: " + colorHex;
        final Guild guild = context.getGuild();
        final List<Role> colorRoleToAdd = guild.getRolesByName(name, false);
        final List<Role> colorRoleToRemove = author.getRoles().stream().filter(role -> role.getName().startsWith("Color: ")).collect(Collectors.toList());
        if (!colorRoleToAdd.isEmpty() && colorRoleToRemove.containsAll(colorRoleToAdd)) {
            return FixedMessage.build("You already have this color!");
        }
        final GuildController guildController = guild.getController();
        if (!colorRoleToAdd.isEmpty()) {
            guildController.modifyMemberRoles(author, colorRoleToAdd, colorRoleToRemove).queue(aVoid -> deleteEmptyRoles(author, colorRoleToRemove));
        } else {
            guildController.createRole().setName(name).setColor(newColor).queue(role -> {
                guildController.modifyRolePositions(false).selectPosition(role).moveTo(2).queue();
                guildController.modifyMemberRoles(author, Collections.singletonList(role), colorRoleToRemove).queue(aVoid -> deleteEmptyRoles(author, colorRoleToRemove));
            });
        }

        return FixedMessage.build("Your color has been successfully set to `" + colorHex + "`");
    }

    private void deleteEmptyRoles(Member member, List<Role> rolesRemovedFromMember) {
        for (Role role : rolesRemovedFromMember) {
            final List<Member> membersWithRole = member.getGuild().getMembersWithRoles(role);
            if (membersWithRole.size() == 1 && membersWithRole.contains(member)) {
                role.delete().queue();
            }
        }
    }

    public static String colorHex(Color color) {
        return String.format("0x%6s", Integer.toHexString(color.getRGB() & 0xFFFFFF).toUpperCase()).replace(' ', '0');
    }
}
