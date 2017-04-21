package samurai.entities.impl;

import samurai.database.DatabaseSingleton;
import samurai.database.Entry;
import samurai.database.SDatabase;
import samurai.entities.manager.GuildManager;
import samurai.entities.manager.impl.GuildManagerImpl;
import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * manages primary specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class GuildImpl implements SGuild {

    private String prefix;
    private long guildId;
    private long[] userIds;
    private List<Player> players;
    private List<Chart> charts;
    private long enabledCommands;
    private List<Entry<Long, GameMode>> channelFilters;
    private boolean sorted;
    private GuildManager manager;

    public GuildImpl(long guildId, String prefix, long commands, long[] userIds) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.enabledCommands = commands;
        this.userIds = userIds;
        sorted = false;
    }

    private List<Player> getPlayerList() {
        if (players == null) {
            SDatabase database = DatabaseSingleton.getDatabase();
            players = Arrays.stream(userIds).mapToObj(database::getPlayer).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        }
        return players;
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(getPlayerList());
    }

    @Override
    public Optional<Player> getPlayer(long discordId) {
        return getPlayers().stream().filter(player -> player.getDiscordId() == discordId).findAny();
    }

    private void sort() {
        getPlayerList().sort(Comparator.comparingInt(Player::getRankG));
        sorted = true;
    }

    @Override
    public int getPlayerCount() {
        return getPlayers().size();
    }

    private List<Chart> getChartList() {
        if (charts == null) {
            charts = DatabaseSingleton.getDatabase().getGuildCharts(guildId);
        }
        return charts;
    }

    @Override
    public List<Chart> getCharts() {
        return Collections.unmodifiableList(getChartList());
    }

    @Override
    public long getGuildId() {
        return guildId;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GuildImpl guild = (GuildImpl) obj;

        return guildId == guild.guildId
                && enabledCommands == guild.enabledCommands
                && prefix.equals(guild.prefix);
    }

    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + (int) (guildId ^ (guildId >>> 32));
        result = 31 * result + (int) (enabledCommands ^ (enabledCommands >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("SamuraiGuild{%n\tprefix='%s'%n\tguildId=%d%n\tusers=%n%s%n\tcharts=%s%n}", prefix, guildId, players, charts);
    }

    @Override
    public long getEnabledCommands() {
        return enabledCommands;
    }

    private List<Entry<Long, GameMode>> getChannelFilterList() {
        if (channelFilters == null) {
            channelFilters = DatabaseSingleton.getDatabase().getGuildFilters(guildId);
        }
        return channelFilters;
    }

    @Override
    public List<Entry<Long, GameMode>> getChannelFilters() {
        return Collections.unmodifiableList(getChannelFilterList());
    }

    @Override
    public int getRankL(Player player) {
        if (!sorted) sort();
        return players.indexOf(player);
    }

    @Override
    public GuildManager getManager() {
        return manager == null ? (manager = new GuildManagerImpl(this)) : manager;
    }

    public boolean addPlayer(Player p) {
        sorted = false;
        return getPlayerList().add(p);
    }

    public boolean addChart(Chart chart) {
        final List<Chart> chartList = getChartList();
        if (!chartList.contains(chart)) chartList.add(chart);
        return true;
    }

    public boolean removePlayer(Player p) {
        return players.remove(p);
    }

    public boolean setEnabledCommands(long enabledCommands) {
        this.enabledCommands = enabledCommands;
        return true;
    }

    public boolean setPrefix(String prefix) {
        this.prefix = prefix;
        return true;
    }

    public boolean addDedicatedChannel(long channelId, GameMode mode) {
        return getChannelFilterList().add(new Entry<>(channelId, mode));
    }

    public void setUsers(long[] users) {
        this.userIds = users;
    }
}
