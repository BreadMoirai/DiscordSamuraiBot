package samurai.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import samurai.Bot;
import samurai.SamuraiDiscord;
import samurai.command.general.Help;

import java.time.OffsetDateTime;
import java.util.Collections;

/**
 * @author TonTL
 * @version 5.x - 4/6/2017
 */
@Ignore
public class CommandTest {

    private static JDA client;
    private static SamuraiDiscord samurai;
    private static String testingChannelId = "285315282560614400";
    private static String guildId = "233097800722808832";
    private static TextChannel testingChannel;


    @BeforeClass
    public static void initializeBot() {
        Bot.start();
        samurai = Bot.getShards().stream().filter(samuraiDiscord -> samuraiDiscord.getClient().getTextChannelById(testingChannelId) != null).findAny().orElseThrow(() -> new ExceptionInInitializerError("Could not find client"));
        client = samurai.getClient();
        testingChannel = client.getTextChannelById(testingChannelId);
    }

    private static CommandContext createContext(String message) {
        final Guild guildById = client.getGuildById(guildId);
        if (guildById == null) {
            throw new NullPointerException("Could not find DreadMoirai'sSamurais");
        }
        return new CommandContext("", "", guildById.getMemberById("232703415048732672"), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), message, Collections.emptyList(), 233097800722808832L, 285315282560614400L, 299706056739913740L, testingChannel, OffsetDateTime.now());
    }

    @Test
    public void testHelp() {
        final Help help = new Help();

        System.out.println("Testing: Help");
        help.setContext(createContext("help"));
        samurai.onCommand(help);
    }

    @Test
    public void testPrefix() {

    }
}
