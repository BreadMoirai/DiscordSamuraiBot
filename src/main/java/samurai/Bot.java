package samurai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import samurai.command.CommandFactory;
import samurai.database.Database;
import samurai.util.BotUtil;

import java.util.ArrayList;
import java.util.List;
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
        DEFAULT_PREFIX = config.getString("samurai.prefix");
    }

    public static void main(String[] args) {
        Bot.start();
    }

    public static void start() {
        final Config config = ConfigFactory.load();

        for (int i = 0; i < SHARD_COUNT; i++)
        shards.add(new SamuraiDiscord(
                new JDABuilder(AccountType.BOT)
                .setToken(config.getString("samurai.token"))
                .setAudioEnabled(false)
                .useSharding(i,SHARD_COUNT)));


        System.out.println("Initializing " + CommandFactory.class.getSimpleName());
        CommandFactory.initialize();
        BotUtil.initialize(shards);
        Runtime.getRuntime().addShutdownHook(new Thread(Bot::shutdown));
    }

    public static void shutdown() {
        System.out.println("Shutting Down");
        Database.close();
        for (SamuraiDiscord samurai : shards) {
            samurai.shutdown();
        }
        System.out.println("Complete");
    }


    public static void onPrivateMessageEvent(GenericPrivateMessageEvent event) {
        shards.forEach(samuraiDiscord -> samuraiDiscord.getMessageManager().onPrivateMessageEvent(event));
    }

    public static int getPlayerCount() {
        return shards.stream().map(SamuraiDiscord::getClient).map(JDA::getUsers).mapToInt(List::size).reduce(Integer::sum).orElse(0);
    }

    public static int getGuildCount() {
        return shards.stream().map(SamuraiDiscord::getClient).map(JDA::getGuilds).mapToInt(List::size).reduce(Integer::sum).orElse(0);
    }

    public static ArrayList<SamuraiDiscord> getShards() {
        return shards;
    }
}
