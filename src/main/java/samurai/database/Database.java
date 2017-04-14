package samurai.database;

import samurai.database.impl.SDatabaseImpl;

import java.sql.SQLException;

/**
 * @author TonTL
 * @version 3/23/2017
 */
public class Database {

    private static final SDatabase database;
    private static boolean open;

    static {
        try {
            database = new SDatabaseImpl();
            open = true;
        } catch (SQLException e) {
            SQLUtil.printSQLException(e);
            throw new ExceptionInInitializerError("Failed to initialize database");
        }
    }


    public static SDatabase getDatabase() {
        if (!open) throw new UnsupportedOperationException("Cannot retrieve closed database.");
        return database;
    }

    public static void close() {
        if (!open) throw new UnsupportedOperationException("Database is already closed");
        if (database != null) {
            open = false;
            database.close();
        }
    }
}
