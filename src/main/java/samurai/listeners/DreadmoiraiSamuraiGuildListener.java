package samurai.listeners;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class DreadmoiraiSamuraiGuildListener extends ListenerAdapter {

    private static final String GUILD_ID = "233097800722808832";
    private static final String PEASANT_ROLE = "267924616574533634";

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild().getId().equals(GUILD_ID)) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(PEASANT_ROLE));
        }
    }
}
