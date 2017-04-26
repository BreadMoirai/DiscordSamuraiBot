package samurai.database;

/**
 * @author TonTL
 * @version 3/23/2017
 */
public class DatabaseSingleton {

    private static final SDatabase database;
    private static boolean open;

    static {
//        try {
            database = null;
            open = true;
//        } catch (SQLException e) {
//            SQLUtil.printSQLException(e);
//            throw new ExceptionInInitializerError("Failed to initialize database");
//        }
    }


    public static SDatabase getDatabase() {
        if (!open) throw new UnsupportedOperationException("Cannot retrieve closed database.");
        return database;
    }

    public static void close() {
        if (!open) throw new UnsupportedOperationException("Database is already closed");
        else {
            open = false;
            database.close();
        }
    }
}
