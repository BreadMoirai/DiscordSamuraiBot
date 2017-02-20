package samurai;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import samurai.action.admin.Groovy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Main Class
 * Initializes Samurai bot
 */
public class Bot {

    public static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";
    public static final long initializationTime = System.currentTimeMillis();
    public static final User self;
    public static final String SOURCE_GUILD = "233097800722808832";
    public static final String BOT_ID = "270044218167132170";
    private static final String TOKEN = "MjcwMDQ0MjE4MTY3MTMyMTcw.C1yJ0Q.oyQMo7ZGXdaq2K3P43NMwOO8diM";
    private static final String LOG_CHANNEL = "281911114265001985";
    private static JDA client;

    static {
        try {
            JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
            SamuraiListener listener = new SamuraiListener();
            client = jdaBuilder
                    .addListener(listener)
                    .setToken(TOKEN)
                    .buildBlocking();
            listener.setJDA(client);
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            logError(e);
            System.exit(1);
        }
        self = client.getSelfUser();
        Groovy.addBinding("client", client);
    }

    private String SELF_TOKEN;

    private Bot() {
        try {
            if (new File(Bot.class.getResource("config.txt").toURI()).exists()) {
                SELF_TOKEN = Files.readAllLines(new File(Bot.class.getResource("config.txt").toURI()).toPath(), StandardCharsets.UTF_8).get(0);
            }
        } catch (URISyntaxException | IOException e) {
            System.err.println("Could not find config.txt file in resource.samurai");
            System.exit(1);
        }
        try {
            new JDABuilder(AccountType.CLIENT)
                    .setToken(SELF_TOKEN)
                    .addListener(new SelfListener())
                    .buildAsync();
        } catch (LoginException | IllegalArgumentException | RateLimitedException e) {
            log("Failed");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Bot();
    }

    public static void logError(Throwable e) {
        client.getTextChannelById(LOG_CHANNEL).sendMessage(new MessageBuilder()
                .append("```\n")
                .append(ExceptionUtils.getStackTrace(e))
                .append("\n```")
                .build()).queue();
        e.printStackTrace();
    }

    public static void log(String s) {
        client.getTextChannelById(LOG_CHANNEL).sendMessage(s).queue();
    }
}
