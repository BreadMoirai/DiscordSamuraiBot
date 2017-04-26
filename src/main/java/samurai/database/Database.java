package samurai.database;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jdbi.v3.core.Jdbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/20/2017
 */
public class Database {

    private static final String PROTOCOL = "jbdc:derby:";
    private static final String DB_NAME;

    private final Jdbi jdbi;

    static {
        final Config config = ConfigFactory.load();
        DB_NAME = config.getString("database.name");
    }

    public Database() throws SQLException {
        testConnection();
        jdbi = Jdbi.create(PROTOCOL + DB_NAME);
    }

    public <T> T getManager(Class<T> manager) {
        return jdbi.onDemand(manager);
    }


    private void testConnection() throws SQLException {
        Connection initialConnection = null;
        try {
            initialConnection = DriverManager.getConnection(PROTOCOL + DB_NAME + ';');
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResource("databaseInitializer.txt").openStream()))) {
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
