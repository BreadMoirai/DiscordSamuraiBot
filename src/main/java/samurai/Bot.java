package samurai;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.security.auth.login.LoginException;

/**
 * Main Class
 * Initializes Samurai bot
 */
public class Bot {

    public static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";
    public static final long initializationTime = System.currentTimeMillis();
    private static final String TOKEN = "MjcwMDQ0MjE4MTY3MTMyMTcw.C1yJ0Q.oyQMo7ZGXdaq2K3P43NMwOO8diM";
    public static User self;
    private static JDA client;

    private Bot() {
        try {
            JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
            SamuraiListener listener = new SamuraiListener();
            client = jdaBuilder
                    .addListener(listener)
                    .setToken(TOKEN)
                    .buildBlocking();
            listener.setJDA(client);
            self = client.getSelfUser();
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            log(e);
        }
    }

    public static void main(String[] args) {
        new Bot();
    }

    public static void log(Exception e) {
        client.getTextChannelById("281911114265001985").sendMessage(new MessageBuilder()
                .append("```\n")
                .append(ExceptionUtils.getStackTrace(e))
                .append("\n```")
                .build()).queue();
        e.printStackTrace();
    }
}
