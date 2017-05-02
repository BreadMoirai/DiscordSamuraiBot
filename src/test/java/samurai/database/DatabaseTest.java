package samurai.database;

import org.json.JSONObject;
import org.junit.*;
import samurai.api.OsuAPI;
import samurai.command.music.Play;
import samurai.database.dao.PlayerDao;
import samurai.database.objects.PlayerBean;
import samurai.database.objects.PlayerBuilder;

import java.time.Instant;

/**
 * @author TonTL
 * @version 5.x - 3/24/2017
 */
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
    public static void testPlayer() {
        final JSONObject dreadMoirai = OsuAPI.getUserJSON("DreadMoirai");
        Assert.assertNotNull(dreadMoirai);
        final PlayerBean playerBean = new PlayerBuilder()
                .setUserId(12345678L)
                .setOsuId(dreadMoirai.getInt("user_id"))
                .setOsuName(dreadMoirai.getString("username"))
                .setAccuracy(dreadMoirai.getDouble("accuracy"))
                .setRawPP(dreadMoirai.getDouble("pp_raw"))
                .setCountryRank(dreadMoirai.getInt("pp_country_rank"))
                .setGlobalRank(dreadMoirai.getInt("pp_rank"))
                .setPlayCount(dreadMoirai.getInt("playcount"))
                .setLastUpdated(Instant.now().getEpochSecond())
                .create();

        final PlayerBean playerBean1 = Database.get().<PlayerDao, PlayerBean>openDao(PlayerDao.class, dao -> dao.getById(12345678L).build());

        System.out.println(playerBean);
        System.out.println(playerBean1);
        Assert.assertEquals(playerBean, playerBean1);

    }
}