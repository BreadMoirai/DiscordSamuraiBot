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

import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.github.breadmoirai.samurai.util.HelpCommand;
import com.github.breadmoirai.samurai.util.ShutdownCommand;
import com.github.breadmoirai.samurai7.core.impl.SamuraiClientBuilder;
import com.github.breadmoirai.samurai7.database.Database;
import com.github.breadmoirai.samurai7.waiter.EventWaiter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
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

        final Config config = ConfigFactory.load();

        AnnotatedEventManager eventManager = new SamuraiClientBuilder()
                .addAdminModule(member -> member.getUser().getIdLong() == 232703415048732672L
                        || (member.canInteract(member.getGuild().getSelfMember()) && member.hasPermission(Permission.KICK_MEMBERS)))
                .addDefaultPrefixModule("[")
                .addSourceModule(233097800722808832L)
                .addModule(new MusicModule(30, config.getString("api.google"), "SamuraiDiscordBot"))
                .configure(ceb -> ceb.registerCommand(ShutdownCommand.class))
                .configure(ceb -> ceb.registerCommand(HelpCommand.class))
                .buildAnnotated();

        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(config.getString("bot.testtoken"))
                    .setEventManager(eventManager)
                    .addEventListener(EventWaiter.get())
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }


    }
}
