package samurai.discord.listeners;

import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;
import samurai.entities.SamuraiGuild;
import samurai.osu.tracker.OsuTracker;

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
    public void onUserGameUpdate(UserGameUpdateEvent event) {
        if (event.getGuild().getMember(event.getUser()).getGame().getName().equalsIgnoreCase("osu!")) {
            final SamuraiGuild samuraiGuild = samurai.getGuildManager().getGuild(Long.valueOf(event.getGuild().getId()));
            OsuTracker.register(samuraiGuild, samuraiGuild.getUser(Long.parseLong(event.getUser().getId())));
        }
    }
}
