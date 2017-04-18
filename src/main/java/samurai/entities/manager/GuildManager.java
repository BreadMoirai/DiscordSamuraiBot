package samurai.entities.manager;

import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

/**
 * @author TonTL
 * @version 4/3/2017
 */
public interface GuildManager {
    boolean addPlayer(Player p);

    boolean removePlayer(Player p);

    boolean addNewChart(String name, boolean isSet);

    boolean addChart(int chartId);

    SGuild getGuild();

    boolean setPrefix(String newPrefix);

    boolean setCommands(long newCommands);

    boolean addChannelFilter(long channelId, GameMode mode);

    void addPlayer(long authorId, String username, int user_id, double pp_raw, int pp_rank, int pp_country_rank);

    void setUsers(long... userID);

    void removeChannelFilter(long idLong);
}
