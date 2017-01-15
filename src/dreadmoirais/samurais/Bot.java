package dreadmoirais.samurais;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Bot {

    private static final String BOT_TOKEN = "MjcwMDQ0MjE4MTY3MTMyMTcw.C1yJ0Q.oyQMo7ZGXdaq2K3P43NMwOO8diM";
    private static JDA jda;

    public static void main(String[] args) {

        try {
            jda = new JDABuilder(AccountType.BOT).addListener(new BotListener()).setToken(BOT_TOKEN).buildBlocking();


        } catch (LoginException | RateLimitedException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
