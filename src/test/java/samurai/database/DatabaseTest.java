package samurai.database;

import org.junit.*;
import samurai.database.dao.PlayerDao;
import samurai.database.objects.PlayerBean;
import samurai.database.objects.PlayerBuilder;
import samurai.osu.OsuAPI;

import java.io.InputStream;

import static org.junit.Assert.*;

@Ignore
public class DatabaseTest {

    private static final long GUILD_ID = 123456789L;
    private static final long CHANNEL_ID = 233097800722808832L;

    @BeforeClass
    public static void databaseInitialization() {
        Assert.assertNotNull("Failed to Initialize Database", Database.get());
    }

    @AfterClass
    public static void databaseClose() {
        Database.close();
    }


    @Test
    public void testPlayer() {
        final PlayerBuilder dreadMoirai = OsuAPI.getPlayer("DreadMoirai");
        Assert.assertNotNull(dreadMoirai);

        dreadMoirai.setDiscordId(123456789L);

        final PlayerBean playerCreated = dreadMoirai.create();

        final PlayerBean playerQueried = Database.get().<PlayerDao, PlayerBean>openDao(PlayerDao.class, dao -> dao.getPlayer(123456789L));

        assertEquals(playerCreated, playerQueried);
    }
}