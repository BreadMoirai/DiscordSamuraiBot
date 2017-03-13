package samurai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.lang3.exception.ExceptionUtils;
import samurai.command.CommandFactory;
import samurai.command.admin.Groovy;
import samurai.core.CommandListener;
import samurai.data.SamuraiDatabase;
import samurai.data.SamuraiStore;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    private static TextChannel logChannel;

    private static ArrayList<JDA> shards;

    static {
        START_TIME = System.currentTimeMillis();
        final Config config = ConfigFactory.load();
        SOURCE_GUILD = config.getString("samurai.source_guild");
        ID = config.getString("samurai.id");
        AVATAR = config.getString("samurai.avatar");
        CALLS = new AtomicInteger();
        SENT = new AtomicInteger();

        shards = new ArrayList<>(1);
        SimpleLog.ENABLE_GUI = false;
        SimpleLog.LEVEL = SimpleLog.Level.DEBUG;
    }

    public static void main(String[] args) {
        Bot.start();
    }

    private static void start() {

        try {
            JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);

            final Config config = ConfigFactory.load();
            JDA client = jdaBuilder
                    .setToken(config.getString("samurai.token"))
                    .buildAsync();
            CommandListener listener = new CommandListener(client);
            client.addEventListener(listener);

            System.out.println("Initializing " + CommandFactory.class.getName());
            System.out.println("Keys Found: " + CommandFactory.keySet().size());


            client.getPresence().setGame(Game.of("Shard 1/1"));

            logChannel = client.getTextChannelById(config.getString("samurai.log_channel"));
            shards.add(client);

            Groovy.addBinding("client", client);

            SamuraiDatabase.read();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
        addLogListener();

    }

    public static void stop() {
        for (JDA client : shards) {
            CommandListener l = (CommandListener) client.getRegisteredListeners().get(0);
            client.removeEventListener(l);
            l.stop();
        }
        System.out.println("Shutting Down");
        for (JDA client : shards) client.shutdown();
        try {
            Runtime.getRuntime().exec("cmd /c start xcopy C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\build\\resources\\main\\samurai\\data C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data /d /e /f /h /i /s /y /z /exclude:C:\\Users\\TonTL\\Desktop\\Git\\DiscordSamuraiBot\\src\\main\\resources\\samurai\\data\\exclude.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void logError(Throwable e) {
        logChannel.sendMessage(new MessageBuilder()
                .append("```\n")
                .append(ExceptionUtils.getMessage(e))
                .append("\n```")
                .build()).queue();
        e.printStackTrace();
    }

    public static void log(String s) {
        if (logChannel != null)
            logChannel.sendMessage(s).queue();
    }

    public static User getUser(Long id) {
        return shards.get(0).retrieveUserById(String.valueOf(id)).complete();
    }

    private static void addLogListener() {
        SimpleLog.addListener(new SimpleLog.LogListener() {
            @Override
            public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {

                if (logLevel == SimpleLog.Level.WARNING)
                    Bot.log(logLevel.name());
                else if (logLevel == SimpleLog.Level.INFO)
                    Bot.log(message.toString());
            }

            @Override
            public void onError(SimpleLog log, Throwable err) {
                Bot.logError(err);
            }
        });
    }

    public static int getGuildCount() {
        return shards.stream().mapToInt(value -> value.getGuilds().size()).sum();
    }

    public static int getUserCount() {
        return shards.stream().mapToInt(value -> value.getUsers().stream().filter(user -> !user.isBot()).mapToInt(value1 -> 1).sum()).sum();
    }

    public static List<Permission> getChannelPermissions(long channelId) {
        for (JDA client : shards) {
            final TextChannel textChannel = client.getTextChannelById(String.valueOf(channelId));
            if (textChannel == null) continue;
            return textChannel.getGuild().getSelfMember().getPermissions(textChannel);
        }
        return Collections.emptyList();
    }

    public static void refreshGuilds() {
        shards.forEach(jda -> {
            jda.getRegisteredListeners().forEach(jda::removeEventListener);
            jda.getGuilds().forEach(guild -> {
                final long guildId = Long.parseLong(guild.getId());
                if (SamuraiStore.guildExists(guildId))
                SamuraiStore.writeGuild(SamuraiStore.readGuild(guildId));
            });
        });
        stop();
    }
}
