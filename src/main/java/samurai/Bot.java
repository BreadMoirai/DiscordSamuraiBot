/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import samurai.audio.SamuraiAudioManager;
import samurai.command.CommandFactory;
import samurai.database.Database;
import samurai.osu.tracker.OsuTracker;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
    public static final long ID;
    public static final long SOURCE_GUILD;
    public static final String DEFAULT_PREFIX;
    public static final int SHARD_COUNT = 1;
    public static String VERSION;

    private static final ArrayList<JDA> shards;


    static {
        START_TIME = System.currentTimeMillis();

        final Config config = ConfigFactory.load();
        SOURCE_GUILD = config.getLong("samurai.source_guild");
        ID = config.getLong("samurai.id");
        AVATAR = config.getString("samurai.avatar");
        DEFAULT_PREFIX = config.getString("samurai.prefix");

        CALLS = new AtomicInteger();
        SENT = new AtomicInteger();

        shards = new ArrayList<>(1);

        VERSION = "@buildVersion@";
        //STATIC_SCHEDULER = Executors.newScheduledThreadPool(3);
    }

    public static void main(String[] args) {
        Bot.start(args);
    }

    private static void start(String[] args) {
        if (args.length != 0)
            try {
                PrintStream out = new PrintStream(new FileOutputStream("out.txt"));
                System.setOut(out);
                PrintStream err = new PrintStream(new FileOutputStream("err.txt"));
                System.setErr(err);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (VERSION.contains("buildVersion")) {
            VERSION = "Development";
            }

        final Config config = ConfigFactory.load();

        //final SamuraiDiscord samuraiDiscord = new SamuraiDiscord();
        final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(config.getString("samurai.token"))
                .setAudioEnabled(true)
                .addEventListener(new SamuraiDiscord());
        //shards.add(samuraiDiscord);


        try {
            for (int i = 0; i < SHARD_COUNT; i++)
                shards.add(jdaBuilder
                        //.useSharding(i, SHARD_COUNT)
                        .buildAsync());
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }

        System.out.println("Initializing " + CommandFactory.class.getSimpleName());
        CommandFactory.initialize();
    }

    public static void shutdown() {
        System.out.println("Shutting Down");
        SamuraiAudioManager.close();
        Database.close();
        OsuTracker.close();
        for (JDA jda : shards) {
            jda.shutdown();
        }
        System.out.println("Complete");
    }


    public static int getPlayerCount() {
        return shards.stream().map(JDA::getUsers).flatMap(List::stream).distinct().mapToInt(value -> 1).sum();
    }

    public static int getGuildCount() {
        return shards.stream().map(JDA::getGuilds).flatMap(List::stream).distinct().mapToInt(value -> 1).sum();
    }

    public static ArrayList<JDA> getShards() {
        return shards;
    }

    static void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        shards.forEach(jda -> jda.getRegisteredListeners().stream().filter(o -> o instanceof SamuraiDiscord).map(o -> (SamuraiDiscord) o).findAny().ifPresent(samuraiDiscord -> samuraiDiscord.getMessageManager().onPrivateMessageReceived(event)));
    }

}
