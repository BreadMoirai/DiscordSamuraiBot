package samurai.entities.model;

import samurai.database.Entry;
import samurai.entities.manager.GuildManager;

import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4/1/2017
 */
public interface SGuild {
    List<Player> getPlayers();

    Optional<Player> getPlayer(long discordId);

    int getPlayerCount();

    List<Chart> getCharts();

    long getGuildId();

    String getPrefix();

    long getEnabledCommands();

    List<Entry<Long, GameMode>> getChannelFilters();

    int getRankL(Player player);

    GuildManager getManager();
}
