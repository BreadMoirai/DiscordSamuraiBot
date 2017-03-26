package samurai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import samurai.command.CommandFactory;
import samurai.data.SamuraiDatabase;
import samurai.util.BotUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main Class
 * Initializes Samurai bot
 */
public class Bot {

    public static final long START_TIME;
    public static final String AVATAR;
    public static final AtomicInteger CALLS;
    public static final AtomicInteger SENT;
    public static final String ID;
    public static final String SOURCE_GUILD;
    public static final String DEFAULT_PREFIX;
    private static final int SHARD_COUNT = 2;

    private static TextChannel logChannel;

    private static ArrayList<SamuraiDiscord> shards;

    static {
        START_TIME = System.currentTimeMillis();
        final Config config = ConfigFactory.load();
        SOURCE_GUILD = config.getString("samurai.source_guild");
        ID = config.getString("samurai.id");
        AVATAR = config.getString("samurai.avatar");
        CALLS = new AtomicInteger();
        SENT = new AtomicInteger();

        shards = new ArrayList<>(1);
        DEFAULT_PREFIX = "!";
    }

    public static void main(String[] args) {
        Bot.start();
    }

    private static void start() {

        final Config config = ConfigFactory.load();

        for (int i = 0; i < SHARD_COUNT; i++)
        shards.add(new SamuraiDiscord(
                new JDABuilder(AccountType.BOT)
                .setToken(config.getString("samurai.token"))
                .setAudioEnabled(false)
                .useSharding(i,SHARD_COUNT)));


        System.out.println("Initializing " + CommandFactory.class.getSimpleName());
        CommandFactory.initialize();
        SamuraiDatabase.read();
        BotUtil.initialize(shards);

    }

    public static void shutdown() {
        System.out.println("Shutting Down");
        for (SamuraiDiscord samurai : shards) {
            samurai.shutdown();
        }
        System.out.println("Complete");
//        try {
//            Runtime.getRuntime().exec("cmd /c start xcopy C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\build\\resources\\main\\samurai\\data C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data /d /e /f /h /i /s /y /z /exclude:C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data\\exclude.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public static void onPrivateMessageEvent(GenericPrivateMessageEvent event) {
        shards.forEach(samuraiDiscord -> samuraiDiscord.getMessageManager().onPrivateMessageEvent(event));
    }

    public static void refreshGuilds() {
        shards.forEach(samuraiDiscord -> samuraiDiscord.getGuildManager().refresh(samuraiDiscord.getClient().getGuilds()));
        Bot.shutdown();
    }
}
