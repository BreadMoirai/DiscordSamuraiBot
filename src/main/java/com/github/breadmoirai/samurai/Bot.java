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

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.plugins.owner.ApplicationOwnerPlugin;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiterPlugin;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.derby.prefix.DerbyPrefixPlugin;
import com.github.breadmoirai.samurai.plugins.google.GooglePlugin;
import com.github.breadmoirai.samurai.plugins.groovyval.GroovyvalPlugin;
import com.github.breadmoirai.samurai.plugins.music.DispatchableDispatcher;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.github.breadmoirai.samurai.plugins.personal.BreadMoiraiSamuraiPlugin;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import com.github.breadmoirai.samurai.plugins.rollpoll.RollPollPlugin;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * Main Class
 * Initializes Samurai bot
 */
public class Bot {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, SQLException, LoginException {

        final List<String> argList = Arrays.stream(args).map(String::toLowerCase).collect(Collectors.toList());
        if (argList.contains("ext")) {
            PrintStream out = new PrintStream(new FileOutputStream("out.txt"), false, "UTF-8");
            System.setOut(out);
            PrintStream err = new PrintStream(new FileOutputStream("err.txt"), false, "UTF-8");
            System.setErr(err);
        }
//
//        if (argList.contains("items")) {
//            DerbyDatabase.get().loadItems(true);
//            DerbyDatabase.close();
//            System.exit(0);
//        }

        final Config config = ConfigFactory.load();

        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        BreadBot bread = new BreadBotBuilder()
                .addPlugin(new ApplicationOwnerPlugin())
                .addPlugin(new EventWaiterPlugin(service))
                .addPlugin(new GroovyvalPlugin())
                .addPlugin(new DerbyDatabase("botdata"))
                .addPlugin(new DerbyPointPlugin())
                .addPlugin(new DerbyPrefixPlugin("!"))
                .addPlugin(new MusicPlugin(config.getString("google.key"), service))
                .addPlugin(new RollPollPlugin(service))
                .addPlugin(new BreadMoiraiSamuraiPlugin())
                .addPlugin(new GooglePlugin(config.getString("google.key"), config.getString("google.engine")))
                .addCommand(new ShutdownCommand(service))
                .bindResultHandler(Dispatchable.class, new DispatchableDispatcher())
                .build();


        new JDABuilder(AccountType.BOT)
                .setToken(config.getString("bot.token"))
                .setAudioEnabled(true)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListener(bread)
                .buildAsync();
    }


}
