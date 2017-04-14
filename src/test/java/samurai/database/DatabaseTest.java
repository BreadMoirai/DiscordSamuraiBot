package samurai.database;

import org.junit.*;
import samurai.command.CommandModule;
import samurai.entities.manager.ChartManager;
import samurai.entities.manager.GuildManager;
import samurai.entities.model.Chart;
import samurai.entities.model.Player;
import samurai.entities.model.SGuild;
import samurai.osu.enums.GameMode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        if (Database.getDatabase() == null) {
            Assert.fail("Failed to Initialize Database");
        }
    }

    @Test
    public void databaseTest() {
        Assert.assertNotNull(Database.getDatabase());
    }

    @Test
    public void channelFilterTest() {
        final SDatabase database = Database.getDatabase();
        final Optional<SGuild> guildOptional = database.createGuild(GUILD_ID, 12345L);
        if (!guildOptional.isPresent()) {
            return;
        }
        final SGuild guild = guildOptional.get();
        Assert.assertTrue(guild.getManager().addChannelFilter(CHANNEL_ID, GameMode.MANIA));
        final Optional<GameMode> gameMode = database.getFilter(CHANNEL_ID);
        Assert.assertTrue(gameMode.isPresent());
        Assert.assertEquals(gameMode.get(), GameMode.MANIA);


        Assert.assertTrue(database.putFilter(GUILD_ID, CHANNEL_ID, GameMode.TAIKO));
        final Optional<GameMode> gameMode2 = database.getFilter(CHANNEL_ID);
        Assert.assertNotNull(gameMode2);
        Assert.assertTrue(gameMode2.isPresent());
        Assert.assertEquals(gameMode2.get(), GameMode.TAIKO);

        Assert.assertTrue(database.removeFilter(CHANNEL_ID));
        Assert.assertTrue(database.removeGuild(GUILD_ID));
        Assert.assertFalse(database.getFilter(CHANNEL_ID).isPresent());
    }

    @Test
    public void playerTest() {
        SDatabase database = Database.getDatabase();

        final Optional<Player> playerOptionalA = database.createPlayer(1337L, 666, "The Test of A", 10, 20, 100);
        Assert.assertTrue(playerOptionalA.isPresent());
        final Player playerA = playerOptionalA.get();
        Assert.assertEquals(1337L, playerA.getDiscordId());
        Assert.assertEquals("The Test of A", playerA.getOsuName());


        final Optional<Player> playerOptionalA2 = database.getPlayer(1337L);
        Assert.assertTrue(playerOptionalA2.isPresent());
        Assert.assertEquals(playerA, playerOptionalA2.get());

        Assert.assertTrue(database.removePlayer(1337L));
    }

    @Test
    public void chartTest() {
        SDatabase database = Database.getDatabase();

        final String chartNameA = "Chart Test A";
        final Optional<Chart> chartOptA = database.createChart(chartNameA);
        Assert.assertTrue(chartOptA.isPresent());

        final Chart chartA = chartOptA.get();

        final int chartIdA = chartA.getChartId();
        System.out.println("Chart ID: " + chartIdA);

        final ChartManager managerA = chartA.getManager();
        Assert.assertNotNull(managerA);

        List<Integer> chartIdList = Arrays.asList(1, 2, 17, 4, 8, 9, 13);
        for (Integer value : chartIdList) {
            Assert.assertTrue(managerA.addMapSet(value));
        }

        final Optional<Chart> chartOptA2 = database.getChart(chartIdA);
        Assert.assertTrue(chartOptA2.isPresent());
        final Chart chartA2 = chartOptA2.get();
        Assert.assertEquals(chartNameA, chartA2.getChartName());
        chartA2.getBeatmapIds().forEach(integer -> Assert.assertTrue("Chart(MapSetID) retrieved does not match values inserted", chartIdList.contains(integer)));

        final String newNameA = "Chart Test A2";
        Assert.assertTrue("Name Change", managerA.changeName(newNameA));


        final Optional<Chart> chartOptA3 = database.getChart(chartIdA);
        Assert.assertTrue(chartOptA3.isPresent());
        final Chart chartA3 = chartOptA3.get();
        Assert.assertEquals("Name change not reflected in database", newNameA, chartA3.getChartName());

        final String chartNameB = "Chart Test B";
        final Optional<Chart> chartOptB = database.createChart(chartNameB);
        Assert.assertTrue(chartOptB.isPresent());
        final Chart chartB = chartOptB.get();
        final int chartIdB = chartB.getChartId();
        System.out.println("Chart Id: " + chartIdB);

        Assert.assertEquals(chartIdA + 1, chartIdB);
        Assert.assertTrue("Remove ChartA", database.removeChart(chartIdA));
        Assert.assertTrue("Remove ChartB", database.removeChart(chartIdB));
    }

    @Test
    public void guildTest() {
        SDatabase database = Database.getDatabase();
        final long commands = 33119L;
        final Optional<SGuild> guildOptional = database.createGuild(GUILD_ID, commands);
        Assert.assertTrue(guildOptional.isPresent());
        final SGuild guild = guildOptional.get();
        final GuildManager guildManager = guild.getManager();
        Assert.assertEquals( ">", guild.getPrefix());
        Assert.assertEquals(GUILD_ID, guild.getGuildId());
        Assert.assertEquals(commands, guild.getEnabledCommands());

        Assert.assertTrue(guildManager.setCommands(33120));
    }

    @Test
    public void GuildFilterTests() {
        SDatabase database = Database.getDatabase();

        Assert.assertTrue(database.putFilter(GUILD_ID, CHANNEL_ID, GameMode.MANIA));
        Assert.assertTrue(database.putFilter(GUILD_ID, CHANNEL_ID+100, GameMode.OSU));

        final List<Entry<Long, GameMode>> guildFilters = database.getGuildFilters(GUILD_ID);
        for (Entry<Long, GameMode> entry : guildFilters) {
            if (entry.getValue() == GameMode.MANIA)
                Assert.assertEquals(CHANNEL_ID, entry.getKey().longValue());
            if (entry.getValue() == GameMode.OSU)
                Assert.assertEquals(CHANNEL_ID+100, entry.getKey().longValue());
        }

        Assert.assertTrue(database.removeGuildFilters(GUILD_ID));
        Assert.assertFalse(database.getFilter(CHANNEL_ID).isPresent());
        Assert.assertFalse(database.getFilter(CHANNEL_ID+100).isPresent());
        Assert.assertTrue(database.removeGuild(GUILD_ID));
    }

    @Ignore
    @Test
    public void SamuraiGuildTest() {
        SDatabase database = Database.getDatabase();
        Assert.assertTrue(database.createGuild(233097800722808832L, CommandModule.getEnabledAll()).isPresent());
    }


    @AfterClass
    public static void databaseClose() {
        Database.close();
    }
}