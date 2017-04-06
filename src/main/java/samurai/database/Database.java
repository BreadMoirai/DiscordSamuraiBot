package samurai.database;

import samurai.database.impl.SDatabaseImpl;

import java.sql.SQLException;

/**
 * @author TonTL
 * @version 3/23/2017
 */
public class Database {

    private static SDatabase database;


    public static SDatabase getDatabase() {
        if (database == null) {
            try {
                database = new SDatabaseImpl();
            } catch (SQLException e) {
                SQLUtil.printSQLException(e);
            }
        }
        return database;
    }

    public static void close() {
        if (database != null) {
            database.close();
            database = null;
        }
    }
}
