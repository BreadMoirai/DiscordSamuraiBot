package samurai;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.lang3.exception.ExceptionUtils;
import samurai.action.admin.Groovy;
import samurai.data.SamuraiDatabase;

import javax.security.auth.login.LoginException;
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
    static final String SOURCE_GUILD;
    private static final String TOKEN;

    private static TextChannel logChannel;
    private static TextChannel infoChannel;

    private static ArrayList<JDA> shards;

    static {
        START_TIME = System.currentTimeMillis();
        SOURCE_GUILD = "233097800722808832";
        ID = "270044218167132170";
        AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";
        TOKEN = "MjcwMDQ0MjE4MTY3MTMyMTcw.C1yJ0Q.oyQMo7ZGXdaq2K3P43NMwOO8diM";

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
            SamuraiListener listener = new SamuraiListener();
            JDA client = jdaBuilder
                    .addListener(listener)
                    .setToken(TOKEN)
                    .buildBlocking();
            listener.setJDA(client);
            client.getPresence().setGame(Game.of("@Samurai"));
            logChannel = client.getTextChannelById("288157271291068417");
            infoChannel = client.getTextChannelById("288159388374663170");
            shards.add(client);
            Groovy.addBinding("client", client);
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            e.printStackTrace();
        }
        addLogListener();
    }

    public static void stop() {
        for (JDA client : shards) {
            ((SamuraiListener) client.getRegisteredListeners().get(0)).stop();
        }
        SamuraiDatabase.writeSamuraiDatabase();
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
        logChannel.sendMessage(s).queue();
    }

    private static void logInfo(String s) {
        infoChannel.sendMessage(s).queue();
    }

    public static User getUser(Long id) {
        return shards.get(0).getUserById(String.valueOf(id));
    }


    private static void addLogListener() {
        SimpleLog.addListener(new SimpleLog.LogListener() {
            @Override
            public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
                if (logLevel == SimpleLog.Level.WARNING || logLevel == SimpleLog.Level.DEBUG)
                    Bot.log(logLevel.name());
                else if (logLevel == SimpleLog.Level.INFO)
                    Bot.logInfo(message.toString());
            }

            @Override
            public void onError(SimpleLog log, Throwable err) {
                Bot.logError(err);
            }
        });
    }

}
