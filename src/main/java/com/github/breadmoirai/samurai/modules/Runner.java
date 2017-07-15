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
package com.github.breadmoirai.samurai.modules;

import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.github.breadmoirai.samurai7.core.impl.SamuraiClientBuilder;
import com.github.breadmoirai.samurai7.database.Database;
import com.github.breadmoirai.samurai7.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Runner {
    public static void main(String[] args) {
        long ownerId = 232703415048732672L;

        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            Database.create(() -> DriverManager.getConnection("jdbc:derby:MyDatabase;create=true"));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        AnnotatedEventManager eventManager = new SamuraiClientBuilder()
                .addDefaultAdminModule()
                .addDefaultPrefixModule("[")
                .addSourceModule(233097800722808832L)
                .addModule(new MusicModule(30, "AIzaSyCJyTMP8W9QHn5uMoT0cBJIX78znBhaw70", "SamuraiDiscordBot"))
                .configure(ceb -> ceb.registerCommand(ShutdownCommand.class))
                .configure(ceb -> ceb.registerCommand(com.github.breadmoirai.samurai.HelpCommand.class))
                .buildAnnotated();

        try {
            new JDABuilder(AccountType.BOT)
                    .setToken("MzI4Mjc0OTk1NzE1MjQ0MDM0.DESTwQ.ioB0-PtE5eN91WCOpyElKG0bW5o")
                    .setEventManager(eventManager)
                    .addEventListener(EventWaiter.get())
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }


    }
}
