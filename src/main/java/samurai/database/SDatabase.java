package samurai.database;

import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

import java.util.List;
import java.util.Optional;

/**
 * The interface  database.
 *
 * @author TonTL
 * @version 3 /22/2017
 */
public interface SDatabase {

    /**
     * Retrieves a Player from the database
     *
     * @param discordUserId the Snowflake Id of the user
     * @return An Optional Object that contains the player if it exists.
     */
    Optional<Player> getPlayer(long discordUserId);


    /**
     * puts a new player into the database
     * @param discordUserId discord snowflake id of the user
     * @param osuId the osu id of the user
     * @param osuName the osu name of the user
     * @return the player if successful else an empty
     */
    Optional<Player> createPlayer(long discordUserId, int osuId, String osuName, int rankG, int rankC);

    /**
     * Overloads
     * {@link #removePlayer(long)}
     *
     * @param player the player
     * @return the boolean
     */
    default boolean removePlayer(Player player) {
        return removePlayer(player.getDiscordId());
    }


    /**
     * Removes a player from the database
     *
     * @param discordUserId Snowflake id of user to be removed
     * @return success if the operation is a success.
     */
    boolean removePlayer(long discordUserId);

    /**
     * Retrieves a channel filter from the database
     *
     * @param discordChannelId Snowflake ID of the specified textChannel
     * @return a GameMode if it exists
     */
    Optional<GameMode> getFilter(long discordChannelId);

    /**
     * Adds or Updates a filter to the database
     *
     * @param discordChannelId Snowflake TextChannel ID
     * @param mode             Gamemode to filter the channel to
     * @return true if success
     */
    boolean putFilter(long discordGuildId, long discordChannelId, GameMode mode);

    /**
     * Removes a filter from the database
     *
     * @param discordChannelId Snowflake TextChannel ID
     * @return true if the entry was removed else false if operation failed or entry was not found;
     */
    boolean removeFilter(long discordChannelId);

    /**
     * Retrieves the charts associated with a guild
     *
     * @param guildId Snowflake Guild ID
     * @return a list of charts
     */
    List<Chart> getGuildCharts(long guildId);


    /**
     * Retrieves specified chart.
     *
     * @param chartId the chart id
     * @return the chart if it exists
     */
    Optional<Chart> getChart(int chartId);


    /**
     * Update chart boolean.
     *
     * @param chartId the chart id
     * @param name    the name
     * @return the boolean
     */
    boolean updateChart(int chartId, String name);

    /**
     * Creates a new chart and returns it
     *
     * @param name the name of the chart
     * @param isSet true if is a Set
     * @return an Optional Chart that contains null if a database error has occurred
     */
    Optional<Chart> createChart(String name, boolean isSet);

    /**
     * Creates a new chart as type set and returns it
     *
     * @param name the name of the chart
     * @return an Optional Chart that contains null if a database error has occurred
     */
    default Optional<Chart> createChart(String name) {
        return createChart(name, true);
    }

    /**
     * Removes the chart from the database
     *
     * @param chartId the chart id
     * @return the boolean
     */
    boolean removeChart(int chartId);


    /**
     * Put chart map boolean.
     *
     * @param chartId the chart id
     * @param mapSetId   the set or map id
     * @return the boolean
     */
    boolean putChartMap(int chartId, int mapSetId);

    /**
     * Gets guild.
     *
     * @param guildId    the guild id
     * @param userIDList the user id list
     * @return the guild
     */
    Optional<SGuild> getGuild(long guildId, List<Long> userIDList);

    /**
     * creates a new guild and returns it
     *
     * @param guildId Guild's snowflake id
     * @return the boolean
     */
    Optional<SGuild> createGuild(long guildId, long commands);

    /**
     * Put guild chart boolean.
     *
     * @param guildId the guild id
     * @param chartId the chart id
     * @return the boolean
     */
    boolean putGuildChart(long guildId, int chartId);

    /**
     * Remove guild chart boolean.
     *
     * @param guildId the guild id
     * @param chartId the chart id
     * @return the boolean
     */
    boolean removeGuildChart(long guildId, int chartId);


    String getPrefix(long discordGuildId);

    boolean updateGuildPrefix(long guildId, String newPrefix);

    boolean updateGuildCommands(long guildId, long newCommands);

    void close();

    boolean removeGuildFilters(long guildId);

    List<Entry<Long, GameMode>> getGuildFilters(long guildId);

    boolean removeGuild(long guildId);

    /**
     * Debugging only
     */
    void reset();
}
