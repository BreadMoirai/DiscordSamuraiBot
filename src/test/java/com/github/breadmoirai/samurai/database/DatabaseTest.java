package com.github.breadmoirai.samurai.database;

import com.github.breadmoirai.samurai.command.CommandModule;
import com.github.breadmoirai.samurai.database.dao.PlayerDao;
import com.github.breadmoirai.samurai.database.objects.GuildBuilder;
import com.github.breadmoirai.samurai.database.objects.Player;
import com.github.breadmoirai.samurai.database.objects.PlayerBuilder;
import com.github.breadmoirai.samurai.database.objects.SamuraiGuild;
import com.github.breadmoirai.samurai.osu.OsuAPI;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatabaseTest {

    private static final long GUILD_ID = 123456789L;
    private static final long CHANNEL_ID = 233097800722808832L;
    private static final long USER_ID = 123456789L;

    @BeforeClass
    public static void databaseInitialization() {
        Assert.assertNotNull("Failed to Initialize Database", DerbyDatabase.get());
    }

    @AfterClass
    public static void databaseClose() {
        DerbyDatabase.close();
    }

    @Before
    @After
    public void clearEntries() {
        DerbyDatabase.get().getPlayer(USER_ID).ifPresent(player -> player.getUpdater().destroy());
        DerbyDatabase.get().getGuild(GUILD_ID).ifPresent(samuraiGuild -> samuraiGuild.getUpdater().destroy());
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


        final Player playerQueried = DerbyDatabase.get().<PlayerDao, Player>openDao(PlayerDao.class, dao -> dao.getPlayer(USER_ID));
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

        Optional<SamuraiGuild> guildOptional = DerbyDatabase.get().getGuild(GUILD_ID);
        assertTrue(guildOptional.isPresent());
        SamuraiGuild guildQueried = guildOptional.get();
        guildQueried.getPlayers();
        System.out.println("guildQueried = " + guildQueried);
        assertEquals(guildCreated, guildQueried);
        assertTrue(guildQueried.getPlayers().contains(playerCreated));

        //assertEquals(guildQueried.getPrefix(), Database.get().getPrefix(GUILD_ID));

    }
}