package samurai.entities.impl;

import org.apache.commons.lang3.tuple.Pair;
import samurai.database.Entry;
import samurai.entities.manager.GuildManager;
import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

import java.util.List;
import java.util.Optional;

/**
 * manages primary specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SGuildImpl implements SGuild {
    private long guildId;
    private String prefix;
    private List<Player> players;
    private List<Chart> charts;
    private List<Pair<Long, GameMode>> channelFilters;
    private long modules;

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    public void setChannelFilters(List<Pair<Long, GameMode>> channelFilters) {
        this.channelFilters = channelFilters;
    }

    public void setModules(long modules) {
        this.modules = modules;
    }

    @Override
    public List<Player> getPlayers() {
        return null;
    }

    @Override
    public Optional<Player> getPlayer(long discordId) {
        return null;
    }

    @Override
    public int getPlayerCount() {
        return 0;
    }

    @Override
    public List<Chart> getCharts() {
        return null;
    }

    @Override
    public long getGuildId() {
        return 0;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public long getEnabledCommands() {
        return 0;
    }

    @Override
    public List<Entry<Long, GameMode>> getChannelFilters() {
        return null;
    }

    @Override
    public int getRankL(Player player) {
        return 0;
    }

    @Override
    public GuildManager getManager() {
        return null;
    }
}
