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
package samurai.database;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.events.ReadyEvent;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import samurai.Bot;
import samurai.command.CommandContext;
import samurai.command.CommandModule;
import samurai.database.dao.GuildDao;
import samurai.database.dao.PlayerDao;
import samurai.database.dao.PointDao;
import samurai.database.objects.GuildBuilder;
import samurai.database.objects.Player;
import samurai.database.objects.SamuraiGuild;
import samurai.items.ItemType;
import samurai.points.PointSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Database {

    private static final String PROTOCOL = "jdbc:derby:";
    private static final String DB_NAME;

    private static Database self;

    private final Jdbi jdbi;

    static {
        final Config config = ConfigFactory.load();
        DB_NAME = config.getString("database.name");
    }

    public static Database get() {
        if (self == null) {
            try {
                self = new Database();
            } catch (SQLException e) {
                SQLUtil.printSQLException(e);
                throw new ExceptionInInitializerError("Connection could not be opened");
            }
        }
        return self;
    }

    public static void close() {
        self = null;
    }

    private Database() throws SQLException {
        boolean databaseExists = connectElseCreate();
        jdbi = Jdbi.create(PROTOCOL + DB_NAME + ";");
        jdbi.installPlugin(new SqlObjectPlugin());
        if (!databaseExists) loadItems(false);
    }


    public <T, R> R openDao(Class<T> tClass, Function<T, R> function) {
        return jdbi.withExtension(tClass, function::apply);
    }

    public <T> void openDao(Class<T> tClass, Consumer<T> consumer) {
        jdbi.useExtension(tClass, consumer::accept);
    }

    public Optional<SamuraiGuild> getGuild(long guildId) {
        return Optional.ofNullable(jdbi.withExtension(GuildDao.class, extension -> extension.getGuild(guildId)));
    }

    public Optional<Player> getPlayer(long discordId) {
        return Optional.ofNullable(jdbi.withExtension(PlayerDao.class, extension -> extension.getPlayer(discordId)));
    }

    public PointSession getPointSession(long guildId, long memberId) {
        final PointSession pointSession = jdbi.withExtension(PointDao.class, extension -> extension.getSession(memberId, guildId));
        if (pointSession != null) {
            return pointSession;
        } else {
            jdbi.useExtension(PointDao.class, extension -> extension.insertUser(memberId, guildId));
        }
        return jdbi.withExtension(PointDao.class, extension -> extension.getSession(memberId, guildId));
    }

    public void load(ReadyEvent event) {
        List<Long> existingGuilds = jdbi.withExtension(GuildDao.class, GuildDao::getGuilds);
        final ArrayList<Long> foundGuilds = event.getJDA().getGuilds().stream().map(ISnowflake::getIdLong).collect(Collectors.toCollection(ArrayList::new));
        foundGuilds.removeAll(existingGuilds);
        foundGuilds.forEach(guildId -> new GuildBuilder().putPrefix(Bot.info().DEFAULT_PREFIX).putGuildId(guildId).putModules(CommandModule.getDefault()).create());
    }


    public String getPrefix(long guildId) {
        String s = jdbi.withExtension(GuildDao.class, extension -> extension.getPrefix(guildId));
        final String prefix = Bot.info().DEFAULT_PREFIX;
        if (s == null) {
            new GuildBuilder().putPrefix(prefix).putGuildId(guildId).putModules(CommandModule.getDefault()).create();
        } else return s;
        return prefix;
    }

    /**
     * @return true if database exists, false if database was created
     */
    private boolean connectElseCreate() throws SQLException {
        boolean created = false;
        Connection initialConnection = null;
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            final String url = PROTOCOL + DB_NAME + ";";
            initialConnection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            if (e.getErrorCode() == 40000
                    && e.getSQLState().equalsIgnoreCase("XJ004")) {
                initialConnection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true");
                initializeTables(initialConnection);
                created = true;
            } else {
                SQLUtil.printSQLException(e);
            }
        } finally {
            if (initialConnection == null) {
                throw new SQLException("Could not connect nor create SamuraiDerbyDatabase");
            } else {
                initialConnection.close();
            }
        }
        return !created;
    }

    private void initializeTables(Connection connection) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("databaseInitializer.sql")))) {

            final Statement statement = connection.createStatement();
            for (String s : br.lines().collect(Collectors.joining()).split(";")) {
                statement.addBatch(s);
            }
            statement.executeBatch();

            connection.commit();
            statement.close();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadItems(boolean deleteTables) {
        jdbi.useHandle(handle -> {
            try (BufferedReader br = deleteTables ? new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("ItemReset.sql"))) : null;
                 BufferedReader brItems = new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("Items.csv")));
                 BufferedReader brDrops = new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("Drops.csv")))) {

                if (deleteTables)
                    handle.createScript(br.lines().collect(Collectors.joining("\n"))).executeAsSeparateStatements();

                final PreparedBatch itemBatch = handle.prepareBatch("INSERT INTO ItemCatalog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                brItems.readLine();
                String line;
                while ((line = brItems.readLine()) != null) {
                    final String[] values = line.split(",", 0);
                    System.out.println("values = " + Arrays.toString(values));
                    if (values.length != 15) continue;
                    for (int i = 0; i < 15; i++) {
                        final String value = values[i];
                        switch (i) {
                            case 0:
                                if (CommandContext.isNumber(value)) itemBatch.bind(i, Integer.parseInt(value));
                                break;
                            case 1:
                                try {
                                    final ItemType type = ItemType.valueOf(value);
                                    itemBatch.bind(i, type.ordinal());
                                } catch (IllegalArgumentException ignored) {
                                }
                                break;
                            case 2:
                            case 14:
                                if (value == null || value.isEmpty()) itemBatch.bindNull(i, Types.VARCHAR);
                                else itemBatch.bind(i, value);
                                break;
                            case 3:
                                if (value == null || value.isEmpty() || !CommandContext.isNumber(value)) {
                                    itemBatch.bindNull(i, Types.SMALLINT);
                                } else itemBatch.bind(i, Short.parseShort(value));
                                break;
                            default:
                                if (value == null || value.isEmpty() || !CommandContext.isFloat(value))
                                    itemBatch.bindNull(i, Types.DOUBLE);
                                else itemBatch.bind(i, Double.parseDouble(value));
                                break;
                        }
                    }
                    itemBatch.add();
                }
                System.out.println("ItemsInserted: " + Arrays.stream(itemBatch.execute()).sum());

                final PreparedBatch dropBatch = handle.prepareBatch("INSERT INTO DropRate VALUES (?, ?, ?)");
                brDrops.readLine();
                while ((line = brDrops.readLine()) != null) {
                    final String[] values = line.split(",", 0);
                    System.out.println("drops = " + Arrays.toString(values));
                    if (values.length != 3) continue;
                    for (int i = 0; i < 3; i++) {
                        final String value = values[i];
                        dropBatch.bind(i, Integer.parseInt(value));
                    }
                    dropBatch.add();
                }
                System.out.println("ItemDropsInserted: " + Arrays.stream(dropBatch.execute()).sum());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
