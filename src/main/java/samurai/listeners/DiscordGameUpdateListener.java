package samurai.listeners;


import net.dv8tion.jda.core.events.guild.member.GuildMemberGameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class DiscordGameUpdateListener extends ListenerAdapter {


    private SamuraiDiscord samurai;

    public DiscordGameUpdateListener(SamuraiDiscord samurai) {

        this.samurai = samurai;
    }

    @Override
    public void onGuildMemberGameUpdate(GuildMemberGameUpdateEvent event) {

        System.out.println("MemberGame Detected" +event.getMember().getGame().getName());
    }

    @Override
    public void onUserGameUpdate(UserGameUpdateEvent event) {
        System.out.println("UserGame Detected" + event.getGuild().getMember(event.getUser()).getGame().getName());

    }
}
