package samurai.discord.listeners;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.SamuraiDiscord;
import samurai.database.Database;
import samurai.database.Entry;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;
import samurai.osu.tracker.OsuSession;
import samurai.osu.tracker.OsuTracker;

import java.util.Optional;
import java.util.OptionalLong;

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
        if (event.getGuild().getMember(event.getUser()).getGame() == null) return;
        if (event.getGuild().getMember(event.getUser()).getGame().getName().equalsIgnoreCase("osu!")) {
            final long discordUserId = event.getUser().getIdLong();
            final long discordGuildId = event.getGuild().getIdLong();
            final Optional<OsuSession> sessionOptional = OsuTracker.retrieveSession(discordUserId);
            final Optional<SGuild> guildOptional = Database.getDatabase().getGuild(discordGuildId, discordUserId);
            if (guildOptional.isPresent()) {
                final SGuild sGuild = guildOptional.get();
                final OptionalLong any = sGuild.getChannelFilters().stream().filter(longGameModeEntry -> longGameModeEntry.getValue() == GameMode.OSU).mapToLong(Entry::getKey).findAny();
                if (any.isPresent()) {
                    final long discordOutputChannelId = any.getAsLong();
                    final TextChannel outputChannel = event.getGuild().getTextChannelById(String.valueOf(discordOutputChannelId));
                    if (outputChannel != null) {
                        if (sessionOptional.isPresent()) {
                            sessionOptional.get().addChannel(outputChannel);
                        } else {
                            sGuild.getPlayer(discordUserId).ifPresent(player -> OsuTracker.register(player, outputChannel));
                        }
                    } //if the channel Exists
                    else {
                        Database.getDatabase().removeFilter(discordOutputChannelId);
                    }
                } //if they have a channel filter
            } //if there is a sGuild
        }// if game == osu
    }

}
