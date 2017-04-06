package samurai.database;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * @author TonTL
 * @version 4/5/2017
 */
public class DatabaseTestListener extends RunListener {

    @Override
    public void testFailure(Failure failure) throws Exception {
        Database.getDatabase().reset();
        System.err.println("Test failed, Database reset");
    }
}
