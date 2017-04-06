package samurai.database;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author TonTL
 * @version 3/23/2017
 */
public class SQLUtil {

    public static void printSQLException(SQLException e) {

        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).filter(s -> s.contains("samurai")).forEach(System.err::println);
            e = e.getNextException();
        }
    }
}
