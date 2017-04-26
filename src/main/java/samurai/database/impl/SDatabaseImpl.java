/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.database.impl;

import samurai.Bot;
import samurai.database.Entry;
import samurai.database.SDatabase;
import samurai.database.SQLUtil;
import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.entities.impl.ChartImpl;
import samurai.entities.impl.GuildImpl;
import samurai.entities.impl.PlayerImpl;
import samurai.osu.enums.GameMode;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author TonTL
 * @version 3/22/2017
 */
public class SDatabaseImpl implements SDatabase {

    private static final String PROTOCOL = "jdbc:derby:";

    private static final String DB_NAME = "SamuraiDerbyDatabase";

    private final Connection connection;
    private final PreparedStatement psPlayerQuery;
    private final PreparedStatement psPlayerExists;
    private final PreparedStatement psPlayerInsert;
    private final PreparedStatement psPlayerUpdate;
    private final PreparedStatement psPlayerDelete;

    private final PreparedStatement psGuildFilterQuery;
    private final PreparedStatement psGuildFilterDelete;

    private final PreparedStatement psChannelFilterQuery;
    private final PreparedStatement psChannelFilterExists;
    private final PreparedStatement psChannelFilterInsert;
    private final PreparedStatement psChannelFilterUpdate;
    private final PreparedStatement psChannelFilterDelete;

    private final PreparedStatement psGuildChartJoinQuery;

    private final PreparedStatement psChartQuery;
    private final PreparedStatement psChartIdentityQuery;
    private final PreparedStatement psChartInsert;
    private final PreparedStatement psChartExists;
    private final PreparedStatement psChartUpdate;
    private final PreparedStatement psChartDelete;
    private final PreparedStatement psChartCustomInsert;

    private final PreparedStatement psGuildChartExists;
    private final PreparedStatement psGuildChartInsert;
    private final PreparedStatement psGuildChartDelete;

    private final PreparedStatement psChartMapExists;
    private final PreparedStatement psChartMapInsert;
    private final PreparedStatement psChartMapDelete;
    private final PreparedStatement psChartMapDeleteAll;

    private final PreparedStatement psGuildQuery;
    private final PreparedStatement psGuildInsert;
    private final PreparedStatement psGuildUpdatePrefix;
    private final PreparedStatement psGuildUpdateCommands;
    private final PreparedStatement psGuildDelete;

    private final PreparedStatement psMapSetInsert;
    private final PreparedStatement psMapSetQuerySet;
    private final PreparedStatement psMapSetQueryMap;


    public SDatabaseImpl() throws SQLException {
        Connection initialConnection = null;
        try {
            initialConnection = DriverManager.getConnection(PROTOCOL + DB_NAME + ';');
        } catch (SQLException e) {
            if (e.getErrorCode() == 40000
                    && e.getSQLState().equalsIgnoreCase("XJ004")) {
                initialConnection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true");
                reset(initialConnection);
            } else {
                SQLUtil.printSQLException(e);
            }
        } finally {
            if (initialConnection == null) {
                throw new SQLException("Could not connect nor create SamuraiDerbyDatabase");
            }
        }
        connection = initialConnection;
        connection.setAutoCommit(false);

        {
            psPlayerQuery = connection.prepareStatement(
                    "SELECT * " +
                            "FROM PLAYER " +
                            "WHERE DISCORDID=?"
            );
            psPlayerExists = connection.prepareStatement(
                    "SELECT COUNT(1) " +
                            "FROM PLAYER " +
                            "WHERE DISCORDID=?");
            psPlayerInsert = connection.prepareStatement(
                    "INSERT INTO PLAYER " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            psPlayerUpdate = connection.prepareStatement(
                    "UPDATE PLAYER " +
                            "SET " +
                            "PLAYER.GLOBALRANK=?, " +
                            "PLAYER.COUNTRYRANK=?, " +
                            "PLAYER.LASTUPDATED=? " +
                            "WHERE PLAYER.DISCORDID=?"
            );
            psPlayerDelete = connection.prepareStatement(
                    "DELETE FROM PLAYER " +
                            "WHERE DISCORDID=?"
            );
        }
        {
            psChannelFilterQuery = connection.prepareStatement(
                    "SELECT CHANNELFILTER.TYPE " +
                            "FROM CHANNELFILTER " +
                            "WHERE CHANNELID=?");
            psChannelFilterExists = connection.prepareStatement(
                    "SELECT COUNT(1) " +
                            "FROM CHANNELFILTER " +
                            "WHERE CHANNELFILTER.CHANNELID=?"
            );
            psChannelFilterInsert = connection.prepareStatement(
                    "INSERT INTO CHANNELFILTER VALUES (?, ?, ?)"
            );
            psChannelFilterUpdate = connection.prepareStatement(
                    "UPDATE CHANNELFILTER " +
                            "SET CHANNELFILTER.TYPE=?" +
                            "WHERE CHANNELFilter.CHANNELID=?"
            );
            psChannelFilterDelete = connection.prepareStatement(
                    "DELETE FROM CHANNELFILTER " +
                            "WHERE CHANNELFILTER.CHANNELID=?"
            );
            psGuildFilterQuery = connection.prepareStatement(
                    "SELECT CHANNELFILTER.CHANNELID, CHANNELFILTER.TYPE " +
                            "FROM CHANNELFILTER " +
                            "WHERE GUILDID=?"
            );
            psGuildFilterDelete = connection.prepareStatement(
                    "DELETE FROM CHANNELFILTER " +
                            "WHERE GUILDID=?"
            );
        }
        {
            psGuildChartJoinQuery = connection.prepareStatement(
                    "SELECT CHART.CHARTID, CHART.NAME, CHART.ISSET, CHARTMAP.MAPSETID " +
                            "FROM GUILDCHART " +
                            "INNER JOIN CHART ON GUILDCHART.CHARTID = CHART.CHARTID " +
                            "INNER JOIN CHARTMAP ON CHART.CHARTID = CHARTMAP.CHARTID " +
                            "WHERE GUILDCHART.GUILDID=?"
            );
        }
        {
            psChartQuery = connection.prepareStatement(
                    "SELECT Chart.NAME, Chart.ISSET, ChartMap.MAPSETID " +
                            "FROM CHART " +
                            "INNER JOIN CHARTMAP ON CHART.CHARTID = CHARTMAP.CHARTID " +
                            "WHERE CHART.CHARTID=?");
            psChartExists = connection.prepareStatement(
                    "SELECT COUNT(1) " +
                            "FROM CHART " +
                            "WHERE CHART.CHARTID=?"
            );
            psChartInsert = connection.prepareStatement(
                    "INSERT INTO CHART(NAME, ISSET) VALUES (?, ?)"
            );
            psChartIdentityQuery = connection.prepareStatement(
                    "SELECT IDENTITY_VAL_LOCAL() FROM CHART"
            );
            psChartCustomInsert = connection.prepareStatement(
                    "INSERT INTO CHART VALUES (?, ?, ?)"
            );
            psChartUpdate = connection.prepareStatement(
                    "UPDATE CHART " +
                            "SET CHART.NAME=? " +
                            "WHERE CHART.CHARTID=?"
            );
            psChartDelete = connection.prepareStatement(
                    "DELETE FROM CHART " +
                            "WHERE CHART.CHARTID=?"
            );
        }
        {
            psGuildChartExists = connection.prepareStatement(
                    "SELECT COUNT(1) " +
                            "FROM GUILDCHART " +
                            "WHERE GUILDID=? AND CHARTID=?"
            );
            psGuildChartInsert = connection.prepareStatement(
                    "INSERT INTO GUILDCHART VALUES (?, ?)"
            );
            psGuildChartDelete = connection.prepareStatement(
                    "DELETE FROM GUILDCHART " +
                            "WHERE GUILDCHART.GUILDID=? AND GUILDCHART.CHARTID=?"
            );
        }
        {
            psChartMapExists = connection.prepareStatement(
                    "SELECT COUNT(1) " +
                            "FROM CHARTMAP " +
                            "WHERE CHARTID=? AND MAPSETID=?"
            );
            psChartMapInsert = connection.prepareStatement(
                    "INSERT INTO CHARTMAP(CHARTID, MAPSETID) VALUES (?, ?)"
            );
            psChartMapDeleteAll = connection.prepareStatement(
                    "DELETE FROM CHARTMAP " +
                            "WHERE CHARTID=?"
            );
            psChartMapDelete = connection.prepareStatement(
                    "DELETE FROM CHARTMAP " +
                            "WHERE CHARTMAP.CHARTID=? AND CHARTMAP.MAPSETID=?"
            );
        }
        {
            psGuildQuery = connection.prepareStatement(
                    "SELECT * " +
                            "FROM GUILD " +
                            "WHERE GUILDID=?");
            psGuildInsert = connection.prepareStatement(
                    "INSERT INTO GUILD(GUILDID, COMMANDS) VALUES (?, ?)"
            );
            psGuildUpdatePrefix = connection.prepareStatement(
                    "UPDATE GUILD SET PREFIX=? WHERE GUILDID=?"
            );
            psGuildUpdateCommands = connection.prepareStatement(
                    "UPDATE GUILD SET COMMANDS=? WHERE GUILDID=?"
            );
            psGuildDelete = connection.prepareStatement(
                    "DELETE FROM GUILD WHERE GUILD.GUILDID=?"
            );
        }
        {
            psMapSetInsert = connection.prepareStatement(
                    "INSERT INTO MAPSET VALUES (?, ?, ?)"
            );
            psMapSetQuerySet = connection.prepareStatement(
                    "SELECT MAPID, HASH FROM MAPSET WHERE SETID=?"
            );
            psMapSetQueryMap = connection.prepareStatement(
                    "SELECT SETID, HASH FROM MAPSET WHERE MAPID=?"
            );

        }
    }

    //Player
    @Override
    public Optional<Player> getPlayer(long discordUserId) {
        Player p = null;
        try {
            final ResultSet resultSet = executeQuery(psPlayerQuery, discordUserId);
            if (resultSet.next()) {
                p = new PlayerImpl(discordUserId,
                        resultSet.getInt("OsuID"),
                        resultSet.getString("OsuName"),
                        resultSet.getInt("GlobalRank"),
                        resultSet.getInt("CountryRank"),
                        resultSet.getLong("LastUpdated"),
                        resultSet.getInt("RawPP"));
            }
            resultSet.close();
            psPlayerQuery.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return Optional.ofNullable(p);
    }

    @Override
    public Optional<Player> createPlayer(long discordUserId, int osuId, String osuName, int rankG, int rankC, double rawPP) {
        if (!entryExists(psPlayerExists, discordUserId)
                && (executeUpdate(psPlayerInsert, discordUserId, osuId, osuName, rankG, rankC, Instant.now().getEpochSecond(), rawPP) == 1)
                && (commit() || rollback()))
            return getPlayer(discordUserId);
        return Optional.empty();
    }

    @Override
    public boolean removePlayer(long discordUserId) {
        return executeUpdate(psPlayerDelete, discordUserId) == 1 && (commit() || rollback());
    }

    //endPlayer
    //ChannelFilter
    @Override
    public Optional<GameMode> getFilter(long discordChannelId) {
        GameMode type = null;
        try {
            final ResultSet resultSet = executeQuery(psChannelFilterQuery, discordChannelId);
            if (resultSet.next()) {
                type = GameMode.get(resultSet.getInt("Type"));
            }
            resultSet.close();
            psChannelFilterQuery.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return Optional.ofNullable(type);
    }

    @Override
    public boolean putFilter(long discordGuildId, long discordChannelId, GameMode mode) {
        if (entryExists(psChannelFilterExists, discordChannelId)) {
            return executeUpdate(psChannelFilterUpdate, mode.value(), discordChannelId) == 1 && (commit() || rollback());
        } else {
            return executeUpdate(psChannelFilterInsert, discordChannelId, discordGuildId, mode.value()) == 1 && (commit() || rollback());
        }
    }

    @Override
    public boolean removeFilter(long discordChannelId) {
        return executeUpdate(psChannelFilterDelete, discordChannelId) == 1 && (commit() || rollback());
    }

    //endChannelFilter
    @Override
    public List<Chart> getGuildCharts(long guildId) {
        List<Chart> chartList = new ArrayList<>(5);
        try {
            final ResultSet resultSet = executeQuery(psGuildChartJoinQuery, guildId);
            ChartImpl chart = null;
            while (resultSet.next()) {
                if (chart == null || chart.getChartId() != resultSet.getInt("ChartID")) {
                    if (chart != null) {
                        chartList.add(chart);
                    }
                    chart = new ChartImpl(resultSet.getInt("ChartID"), resultSet.getString("Name"), resultSet.getBoolean("IsSet"));
                }
                chart.addMapId(resultSet.getInt("MapSetId"));
            }
            resultSet.close();
            psGuildChartJoinQuery.clearParameters();
            if (chart != null) chartList.add(chart);
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return chartList;
    }

    //Chart
    @Override
    public boolean updateChart(int chartId, String name) {
        return executeUpdate(psChartUpdate, name, chartId) == 1 && (commit() || rollback());
    }

    @Override
    public Optional<Chart> getChart(int chartId) {
        try {
            final ResultSet resultSet = executeQuery(psChartQuery, chartId);
            ChartImpl chart;
            if (resultSet.next()) {
                chart = new ChartImpl(chartId, resultSet.getString("Name"), resultSet.getBoolean("IsSet"));
                chart.addMapId(resultSet.getInt("MapSetId"));
            } else {
                return null;
            }
            while (resultSet.next()) {
                chart.addMapId(resultSet.getInt("MapSetID"));
            }
            resultSet.close();
            psChartQuery.clearParameters();
            return Optional.of(chart);
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Chart> createChart(String name, boolean isSet) {
        final boolean updateSuccess = executeUpdate(psChartInsert, name, isSet) == 1 && (commit() || rollback());
        if (updateSuccess) {
            try {
                final ResultSet resultSet = executeQuery(psChartIdentityQuery);
                if (resultSet.next()) {
                    final int identity = resultSet.getInt(1);
                    resultSet.close();
                    psChartIdentityQuery.clearParameters();
                    return Optional.of(new ChartImpl(identity, name, isSet));
                }
            } catch (SQLException e) {
                SQLUtil.printSQLException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean removeChart(int chartId) {
        return (executeUpdate(psChartMapDeleteAll, chartId) != -1) && (executeUpdate(psChartDelete, chartId) == 1) && (commit() || rollback());
    }
    //endChart

    @Override
    public boolean putGuildChart(long guildId, int chartId) {
        return !entryExists(psGuildChartExists, guildId, chartId) &&
                executeUpdate(psGuildChartInsert, guildId, chartId) == 1 && (commit() || rollback());
    }

    @Override
    public boolean removeGuildChart(long guildId, int chartId) {
        return entryExists(psGuildChartExists, guildId, chartId) &&
                executeUpdate(psGuildChartDelete, guildId, chartId) == 1 && (commit() || rollback());
    }

    @Override
    public String getPrefix(long discordGuildId) {
        final Optional<SGuild> guild = getGuild(discordGuildId);
        return guild.map(SGuild::getPrefix).orElse(Bot.DEFAULT_PREFIX);
    }

    @Override
    public boolean putChartMap(int chartId, int mapSetId) {
        return executeUpdate(psChartMapInsert, chartId, mapSetId) == 1 && (commit() || rollback());
    }

    @Override
    public Optional<SGuild> getGuild(long guildId, long... userIDList) {
        try {
            final ResultSet resultSet = executeQuery(psGuildQuery, guildId);
            if (resultSet.next()) {
                final SGuild guild = new GuildImpl(
                        guildId,
                        resultSet.getString("Prefix"), resultSet.getLong("Commands"),
                        userIDList);
                resultSet.close();
                psGuildQuery.clearParameters();
                return Optional.of(guild);
            }
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<SGuild> createGuild(long guildId, long commands) {
        if (executeUpdate(psGuildInsert, guildId, commands) == 1 && (commit() || rollback())) {
            return (getGuild(guildId));
        } else return Optional.empty();
    }

    @Override
    public boolean updateGuildPrefix(long guildId, String newPrefix) {
        return executeUpdate(psGuildUpdatePrefix, newPrefix, guildId) == 1 && (commit() || rollback());
    }

    @Override
    public boolean updateGuildCommands(long guildId, long newCommands) {
        return executeUpdate(psGuildUpdateCommands, newCommands, guildId) == 1 && (commit() || rollback());
    }

    @Override
    public boolean removeGuildFilters(long guildId) {
        return executeUpdate(psGuildFilterDelete, guildId) > 0 && (commit() || rollback());
    }

    @Override
    public List<Entry<Long, GameMode>> getGuildFilters(long guildId) {
        List<Entry<Long, GameMode>> entryList = new ArrayList<>(4);
        try {
            final ResultSet resultSet = executeQuery(psGuildFilterQuery, guildId);
            while (resultSet.next()) {
                entryList.add(new Entry<>(resultSet.getLong("ChannelID"), GameMode.get(resultSet.getInt("Type"))));
            }
            resultSet.close();
            psGuildFilterQuery.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return entryList;
    }

    @Override
    public boolean removeGuild(long guildId) {
        return executeUpdate(psGuildDelete, guildId) == 1 && (commit() || rollback());
    }

    @Override
    public boolean putMapSet(int setId, int mapId, String hash) {
        return executeUpdate(psMapSetInsert, mapId, setId, hash) == 1 && (commit() || rollback());
    }

    @Override
    public Optional<Integer> getSet(int mapId) {
        int setId = 0;
        try {
            final ResultSet resultSet = executeQuery(psMapSetQueryMap, mapId);
            if (resultSet.next()) {
                setId = resultSet.getInt("SetID");
            }
            resultSet.close();
            psMapSetQueryMap.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return setId == 0 ? Optional.empty() : Optional.of(setId);
    }


    @Override
    public List<Integer> getMaps(int setId) {
        ArrayList<Integer> mapList = new ArrayList<>(5);
        try {
            final ResultSet resultSet = executeQuery(psMapSetQuerySet, setId);
            while (resultSet.next()) {
                mapList.add(resultSet.getInt("MapID"));
            }
            resultSet.close();
            psMapSetQuerySet.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return mapList;
    }


    public void removeTables() {
        try {
            final Statement statement = connection.createStatement();
            statement.addBatch("DROP TABLE GuildChart");
            statement.addBatch("DROP TABLE ChannelFilter");
            statement.addBatch("DROP TABLE Guild");
            statement.addBatch("DROP TABLE ChartMap");
            statement.addBatch("DROP TABLE Chart");
            statement.addBatch("DROP TABLE Player");
            connection.commit();
            statement.close();
            statement.executeLargeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void reset(Connection connection) {
        try {
            final Statement statement = connection.createStatement();


            statement.addBatch("CREATE TABLE Player\n" +
                    "(\n" +
                    "  DiscordId   BIGINT      NOT NULL PRIMARY KEY,\n" +
                    "  OsuId       INT         NOT NULL UNIQUE,\n" +
                    "  OsuNAME     VARCHAR(16) NOT NULL,\n" +
                    "  GlobalRank  INT,\n" +
                    "  CountryRank INT,\n" +
                    "  LastUpdated BIGINT      NOT NULL,\n" +
                    "  RawPP FLOAT" +
                    ')');
            statement.addBatch("CREATE TABLE Guild (\n" +
                    "  GuildId  BIGINT      NOT NULL PRIMARY KEY,\n" +
                    "  Prefix   VARCHAR(16) NOT NULL DEFAULT '" + Bot.DEFAULT_PREFIX + "',\n" +
                    "  Commands BIGINT      NOT NULL\n" +
                    ") ");
            statement.addBatch("CREATE TABLE Chart (\n" +
                    "  ChartId INT         NOT NULL PRIMARY KEY         GENERATED BY DEFAULT AS IDENTITY\n" +
                    "                                                     ( START WITH 255, INCREMENT BY 1),\n" +
                    "  Name    VARCHAR(32) NOT NULL,\n" +
                    "  IsSet   BOOLEAN     NOT NULL                     DEFAULT TRUE\n" +
                    ") ");
            statement.addBatch("CREATE TABLE ChartMap (\n" +
                    "  ChartId  INT NOT NULL,\n" +
                    "  MapSetId INT NOT NULL,\n" +
                    "  CONSTRAINT ChartMap_PK PRIMARY KEY (ChartId, MapSetId),\n" +
                    "  CONSTRAINT ChartId_FK FOREIGN KEY (ChartId) REFERENCES Chart (ChartId)\n" +
                    ") ");
            statement.addBatch("CREATE TABLE GuildChart (\n" +
                    "  GuildID BIGINT NOT NULL,\n" +
                    "  ChartID INT    NOT NULL,\n" +
                    "  CONSTRAINT GuildChartPK PRIMARY KEY (GuildID, ChartID),\n" +
                    "  CONSTRAINT GuildID_FK2 FOREIGN KEY (GuildID) REFERENCES GUILD (GuildID),\n" +
                    "  CONSTRAINT ChartID_FK2 FOREIGN KEY (ChartID) REFERENCES CHART (ChartID)\n" +
                    ") ");
            statement.addBatch("CREATE TABLE ChannelFilter (\n" +
                    "  ChannelID BIGINT PRIMARY KEY,\n" +
                    "  GuildID   BIGINT   NOT NULL,\n" +
                    "  Type      SMALLINT NOT NULL DEFAULT 0,\n" +
                    "  CONSTRAINT GuildID_FK3 FOREIGN KEY (GuildID) REFERENCES GUILD (GuildID)\n" +
                    ") ");
            statement.addBatch("CREATE TABLE MapSet (\n" +
                    "  MapID INT         NOT NULL,\n" +
                    "  SetID INT         NOT NULL,\n" +
                    "  Hash  VARCHAR(36) NOT NULL,\n" +
                    "  CONSTRAINT MapSet_MapID_PK PRIMARY KEY (MapID)\n" +
                    ") ");
            statement.executeLargeBatch();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
    }

    private boolean commit() {
        try {
            connection.commit();
            return true;
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
            return false;
        }
    }

    private boolean rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return false;
    }

    private static boolean entryExists(PreparedStatement ps, Object... param) {
        try {
            int i = param.length;
            while (i > 0) {
                ps.setObject(i, Objects.requireNonNull(param[--i]));
            }
            final ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                final boolean exists = resultSet.getInt(1) == 1;
                ps.clearParameters();
                resultSet.close();
                return exists;
            }
            resultSet.close();
            ps.clearParameters();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        }
        return false;
    }

    private static ResultSet executeQuery(PreparedStatement ps, Object... param) throws SQLException {
        for (int i = param.length; i > 0; i--) {
            ps.setObject(i, Objects.requireNonNull(param[i - 1]));
        }
        return ps.executeQuery();
    }

    private static int executeUpdate(PreparedStatement ps, Object... param) {
        try {
            int i = param.length;
            while (i > 0) {
                ps.setObject(i, Objects.requireNonNull(param[--i]));
            }
            int valuesChanged = ps.executeUpdate();
            ps.clearParameters();
            return valuesChanged;
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
            return 0;
        }
    }

    @Override
    public void close() {
        try {
            DriverManager.getConnection(PROTOCOL + DB_NAME + ";shutdown=true");

            psPlayerQuery.close();
            psPlayerExists.close();
            psPlayerInsert.close();
            psPlayerUpdate.close();
            psPlayerDelete.close();

            psChannelFilterQuery.close();
            psChannelFilterExists.close();
            psChannelFilterInsert.close();
            psChannelFilterUpdate.close();
            psChannelFilterDelete.close();

            psGuildChartJoinQuery.close();

            psChartQuery.close();
            psChartIdentityQuery.close();
            psChartInsert.close();
            psChartExists.close();
            psChartUpdate.close();
            psChartDelete.close();
            psChartCustomInsert.close();

            psGuildChartExists.close();
            psGuildChartInsert.close();
            psGuildChartDelete.close();

            psChartMapExists.close();
            psChartMapInsert.close();
            psChartMapDelete.close();
            psChartMapDeleteAll.close();

            psGuildQuery.close();
            psGuildInsert.close();

        } catch (SQLException se) {
            if (((se.getErrorCode() == 45000)
                    && ("08006".equals(se.getSQLState())))) {
                // we got the expected exception
                System.out.println("Derby shut down normally");
                // Note that for all database close, the expected
                // SQL state is "XJ015", and the error code is 50000.
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (close failed)
                System.err.println("Derby did not shut down normally");
                SQLUtil.printSQLException(se);
            }
        }
    }


}


