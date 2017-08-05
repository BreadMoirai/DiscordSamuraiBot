/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.github.breadmoirai.bot;

import com.github.breadmoirai.bot.framework.core.SamuraiClientBuilder;
import com.github.breadmoirai.bot.framework.database.Database;
import com.github.breadmoirai.bot.framework.modules.admin.DefaultAdminModule;
import com.github.breadmoirai.bot.framework.modules.owner.OwnerModule;
import com.github.breadmoirai.bot.framework.modules.prefix.DynamicPrefixModule;
import com.github.breadmoirai.bot.framework.modules.source.SourceModule;
import com.github.breadmoirai.bot.framework.waiter.EventWaiter;
import com.github.breadmoirai.bot.modules.catdog.CatDogModule;
import com.github.breadmoirai.bot.modules.item.ItemModule;
import com.github.breadmoirai.bot.modules.music.MusicModule;
import com.github.breadmoirai.bot.modules.points.PointModule;
import com.github.breadmoirai.bot.util.HelpCommand;
import com.github.breadmoirai.bot.util.ShutdownCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.security.auth.login.LoginException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Runner {
    public static void main(String[] args) {
        final long ownerId = 232703415048732672L;

        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            Database.create(() -> DriverManager.getConnection("jdbc:derby:MyDatabase;create=true"));
            Database.get().installPlugin(new SqlObjectPlugin());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        final Config config = ConfigFactory.load();

        AnnotatedEventManager eventManager = new SamuraiClientBuilder()
                .addModule(new DefaultAdminModule(member -> member.getUser().getIdLong() == ownerId || member.canInteract(member.getGuild().getSelfMember()) && member.hasPermission(Permission.KICK_MEMBERS)))
                .addModule(new DynamicPrefixModule("["))
                .addModule(new SourceModule(233097800722808832L))
                .addModule(new OwnerModule(ownerId))
                .addModule(new MusicModule(30, config.getString("api.google"), "SamuraiDiscordBot"))
                .addModule(new CatDogModule())
                .addModule(new PointModule())
                .addModule(new ItemModule())
                .registerCommand(ShutdownCommand.class)
                .registerCommand(HelpCommand.class)
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
