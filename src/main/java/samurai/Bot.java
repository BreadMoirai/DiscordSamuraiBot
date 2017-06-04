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

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import samurai.audio.SamuraiAudioManager;
import samurai.database.Database;
import samurai.osu.tracker.OsuTracker;
import samurai.points.PointTracker;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main Class
 * Initializes Samurai bot
 */
public class Bot {

    private static final int SHARD_COUNT = 1;
    private static final ArrayList<JDA> shards;
    private static BotInfo info;

    static {
        shards = new ArrayList<>(SHARD_COUNT);
    }

    public static void main(String[] args) {
        Bot.start(args);
    }

    private static void start(String[] args) {
        final List<String> argList = Arrays.stream(args).map(String::toLowerCase).collect(Collectors.toList());
        if (argList.contains("ext"))
            try {
                PrintStream out = new PrintStream(new FileOutputStream("out.txt"), false, "UTF-8");
                System.setOut(out);
                PrintStream err = new PrintStream(new FileOutputStream("err.txt"), false, "UTF-8");
                System.setErr(err);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        if (argList.contains("items")) {
            Database.get().loadItems(true);
            Database.close();
            System.exit(0);
        }

        final Config config = ConfigFactory.load();

        final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(config.getString("bot.token"))
                .setAudioEnabled(true)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListener(new SamuraiDiscord());

        try {
            //for (int i = 0; i < SHARD_COUNT; i++)
            shards.add(jdaBuilder.buildAsync());
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        System.out.println("Shutting Down");
        for (JDA shard : shards) {
            shard.removeEventListener(shard.getRegisteredListeners());
        }
        SamuraiAudioManager.close();
        Database.close();
        OsuTracker.close();
        PointTracker.close();
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
        shards.forEach(jda -> jda.getRegisteredListeners().stream().filter(o -> o instanceof SamuraiDiscord).map(o -> (SamuraiDiscord) o).forEach(samuraiDiscord -> samuraiDiscord.getMessageManager().onPrivateMessageReceived(event)));
    }

    public static BotInfo info() {
        return info;
    }

    public static void setInfo(BotInfo info) {
        Bot.info = info;
    }
}
