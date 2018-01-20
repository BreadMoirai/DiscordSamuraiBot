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
package com.github.breadmoirai.discord.bot;

import com.github.breadmoirai.breadbot.framework.BreadBotClient;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotClientBuilder;
import com.github.breadmoirai.database.Database;
import com.github.breadmoirai.discord.bot.modules.item.ItemModule;
import com.github.breadmoirai.discord.bot.modules.music.MusicModule;
import com.github.breadmoirai.discord.bot.modules.points.PointModule;
import com.github.breadmoirai.discord.bot.modules.prefix.DynamicPrefixModule;
import com.github.breadmoirai.discord.bot.util.HelpCommand;
import com.github.breadmoirai.discord.bot.util.ShutdownCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.security.auth.login.LoginException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Runner {
    public static void main(String[] args) {
        final long ownerId = 232703415048732672L;

        try {
            DriverManager.registerDriver(new EmbeddedDriver());
            Database.create(() -> DriverManager.getConnection("jdbc:derby:MyDatabase;create=true"));
            Database.get().installPlugin(new SqlObjectPlugin());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        final Config config = ConfigFactory.load();

        final BreadBotClient breadbot = new BreadBotClientBuilder()
                .putParameterModifier(IfAbsentReply.class, (a, cpb) -> {
                    final String value = a.value();
                    cpb.setRequired(true).setOnAbsentArgument((event, missing) -> event.reply(value));
                })
                .addAdminPlugin(member -> member.getUser().getIdLong() == ownerId || member.canInteract(member.getGuild().getSelfMember()) && member.hasPermission(Permission.KICK_MEMBERS))
                .addPlugin(new DynamicPrefixModule("["))
//                .addModule(new SourceModule(233097800722808832L))
//                .addModule(new OwnerModule(ownerId))
                .addPlugin(new MusicModule(30, config.getString("api.google"), "SamuraiDiscordBot"))
                .addPlugin(new PointModule())
                .addPlugin(new ItemModule())
                .registerCommand(ShutdownCommand.class)
                .registerCommand(HelpCommand.class)
                .buildAnnotated();

        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(config.getString("bot.testtoken"))
                    .addEventListener(breadbot)
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }


    }
}
