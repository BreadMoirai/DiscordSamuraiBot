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
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import samurai.Bot;
import samurai.command.CommandModule;
import samurai.database.dao.GuildDao;
import samurai.database.dao.PlayerDao;
import samurai.database.dao.PointDao;
import samurai.database.objects.GuildBuilder;
import samurai.database.objects.Player;
import samurai.database.objects.SamuraiGuild;
import samurai.points.PointSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        testConnection();
        jdbi = Jdbi.create(PROTOCOL + DB_NAME + ";");
        jdbi.installPlugin(new SqlObjectPlugin());
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
        foundGuilds.forEach(guildId -> new GuildBuilder().putPrefix(Bot.DEFAULT_PREFIX).putGuildId(guildId).putModules(CommandModule.getDefault()).create());
    }


    public String getPrefix(long guildId) {
        String s = jdbi.withExtension(GuildDao.class, extension -> extension.getPrefix(guildId));
        if (s == null) {
            new GuildBuilder().putPrefix(Bot.DEFAULT_PREFIX).putGuildId(guildId).putModules(CommandModule.getDefault()).create();
        } else return s;
        return Bot.DEFAULT_PREFIX;
    }

    private void testConnection() throws SQLException {
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
    }

    private void initializeTables(Connection connection) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("databaseInitializer.sql")))) {
            final Statement statement = connection.createStatement();
            for (String s : br.lines().collect(Collectors.joining()).split(";")) {
                statement.addBatch(s);
            }
            statement.executeLargeBatch();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
