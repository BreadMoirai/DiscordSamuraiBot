package samurai.database;

import org.junit.*;
import samurai.command.CommandModule;
import samurai.database.dao.PlayerDao;
import samurai.database.objects.GuildBuilder;
import samurai.database.objects.Player;
import samurai.database.objects.PlayerBuilder;
import samurai.database.objects.SamuraiGuild;
import samurai.osu.OsuAPI;

import java.util.Optional;

import static org.junit.Assert.*;

public class DatabaseTest {

    private static final long GUILD_ID = 123456789L;
    private static final long CHANNEL_ID = 233097800722808832L;
    private static final long USER_ID = 123456789L;

    @BeforeClass
    public static void databaseInitialization() {
        Assert.assertNotNull("Failed to Initialize Database", Database.get());
    }

    @AfterClass
    public static void databaseClose() {
        Database.close();
    }

    @Before
    @After
    public void clearEntries() {
        Database.get().getPlayer(USER_ID).ifPresent(player -> player.getUpdater().destroy());
        Database.get().getGuild(GUILD_ID).ifPresent(samuraiGuild -> samuraiGuild.getUpdater().destroy());
    }

    @Ignore
    @Test
    public void testPlayer() {
        final PlayerBuilder dreadMoirai = OsuAPI.getPlayer("BreadMoirai");
        Assert.assertNotNull(dreadMoirai);
        dreadMoirai.setDiscordId(USER_ID);

        final Player playerCreated = dreadMoirai.create();
        assertNotNull(playerCreated);
        System.out.println("playerCreated = " + playerCreated);


        final Player playerQueried = Database.get().<PlayerDao, Player>openDao(PlayerDao.class, dao -> dao.getPlayer(USER_ID));
        assertNotNull(playerQueried);
        System.out.println("playerQueried = " + playerQueried);

        assertEquals(playerCreated, playerQueried);
    }

    @Ignore
    @Test
    public void testGuild() {
        final PlayerBuilder dreadMoirai = OsuAPI.getPlayer("BreadMoirai");
        Assert.assertNotNull(dreadMoirai);
        dreadMoirai.setDiscordId(USER_ID);
        final Player playerCreated = dreadMoirai.create();

        SamuraiGuild guildCreated = new GuildBuilder()
                .putGuildId(GUILD_ID)
                .putPrefix("+")
                .putModules(CommandModule.getDefault())
                .create();
        assertNotNull(guildCreated);
        System.out.println("guildCreated = " + guildCreated);

        guildCreated.getUpdater().addPlayer(playerCreated, (short) 0b1111);

        Optional<SamuraiGuild> guildOptional = Database.get().getGuild(GUILD_ID);
        assertTrue(guildOptional.isPresent());
        SamuraiGuild guildQueried = guildOptional.get();
        guildQueried.getPlayers();
        System.out.println("guildQueried = " + guildQueried);
        assertEquals(guildCreated, guildQueried);
        assertTrue(guildQueried.getPlayers().contains(playerCreated));

        //assertEquals(guildQueried.getPrefix(), Database.get().getPrefix(GUILD_ID));

    }
}