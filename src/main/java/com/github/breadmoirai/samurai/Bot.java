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
package com.github.breadmoirai.samurai;

import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.samurai.osu.tracker.OsuTracker;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.github.breadmoirai.samurai.points.PointTracker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
            DerbyDatabase.get().loadItems(true);
            DerbyDatabase.close();
            System.exit(0);
        }

        final Config config = ConfigFactory.load();

        new BreadBotBuilder()
//                .addPlugin(new DerbyPrefixPlugin("!"))
                .addPlugin(new MusicPlugin(config.getString("api.google")));






        final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(config.getString("bot.token"))
                .setAudioEnabled(true)
                .addEventListener();

        try {
            jdaBuilder.buildAsync();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        System.out.println("Shutting Down");
        for (JDA shard : shards) {
            shard.removeEventListener(shard.getRegisteredListeners());
        }
        DerbyDatabase.close();
        OsuTracker.close();
        PointTracker.close();
        for (JDA jda : shards) {
            jda.shutdown();
        }
        System.out.println("Complete");
    }


}
