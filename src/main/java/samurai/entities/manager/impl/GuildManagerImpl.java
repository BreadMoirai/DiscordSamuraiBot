package samurai.entities.manager.impl;

import samurai.database.DatabaseSingleton;
import samurai.database.SDatabase;
import samurai.entities.manager.GuildManager;
import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.entities.impl.SGuildImpl;
import samurai.osu.enums.GameMode;

import java.util.Optional;

/**
 * @author TonTL
 * @version 4/1/2017
 */
public class GuildManagerImpl implements GuildManager {

    private SGuildImpl guild;
    private SDatabase database;

    public GuildManagerImpl(SGuildImpl guild) {
        this.guild = guild;
        database = DatabaseSingleton.getDatabase();
    }

    @Override
    public boolean addPlayer(Player p) {
        return guild.addPlayer(p);
    }

    @Override
    public boolean removePlayer(Player p) {
        return guild.removePlayer(p) && database.removePlayer(p);
    }

    @Override
    public boolean addNewChart(String name, boolean isSet) {
        final Optional<Chart> chart = database.createChart(name, isSet);
        return chart.filter(chart1 -> database.putGuildChart(guild.getGuildId(), chart1.getChartId()) && guild.addChart(chart1)).isPresent();
    }

    @Override
    public boolean addChart(int chartId) {
        final Optional<Chart> chart = database.getChart(chartId);
        return chart.filter(chart1 -> database.putGuildChart(guild.getGuildId(), chartId) && guild.addChart(chart1)).isPresent();
    }


    @Override
    public SGuild getGuild() {
        return guild;
    }

    @Override
    public boolean setPrefix(String newPrefix) {
        return database.updateGuildPrefix(guild.getGuildId(), newPrefix)
                && guild.setPrefix(newPrefix);
    }

    @Override
    public boolean setCommands(long newCommands) {
        return database.updateGuildCommands(guild.getGuildId(), newCommands)
                && guild.setEnabledCommands(newCommands);

    }

    @Override
    public boolean addChannelFilter(long channelId, GameMode mode) {
        return database.putFilter(guild.getGuildId(), channelId, mode)
                && guild.addDedicatedChannel(channelId, mode);
    }

    @Override
    public void addPlayer(long discordUserId, String osuName, int osuId, double rawPP, int rankG, int rankC) {
        final Optional<Player> player = DatabaseSingleton.getDatabase().createPlayer(discordUserId, osuId, osuName, rankG, rankC, rawPP);
        player.ifPresent(this::addPlayer);
    }

    @Override
    public void setUsers(long... userID) {
        guild.setUsers(userID);
    }

    @Override
    public void removeChannelFilter(long channelId) {
        DatabaseSingleton.getDatabase().removeFilter(channelId);
    }
}
