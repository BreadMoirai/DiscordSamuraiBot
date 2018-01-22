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

import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class BotInfo implements CommandPlugin, net.dv8tion.jda.core.hooks.EventListener {

    public final long START_TIME = Instant.now().getEpochSecond();
    ;
    public final AtomicInteger CALLS = new AtomicInteger();
    public final AtomicInteger SENT = new AtomicInteger();

    public long ID;
    public String AVATAR;
    public long SOURCE_GUILD;
    public String DEFAULT_PREFIX;
    public long OWNER;
    public String URL;
    public String VERSION;

    public String getUserAgent() {
        return String.format("DiscordBot (%s, %s)", URL, VERSION);
    }

    @Override
    public void initialize(BreadBotBuilder builder) {

    }


    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            JDA client = event.getJDA();

            final Config config = ConfigFactory.load();
            SOURCE_GUILD = config.getLong("bot.source_guild");
            DEFAULT_PREFIX = config.getString("bot.prefix");
            OWNER = config.getLong("owner.id");
            URL = config.getString("bot.url");


            AVATAR = client.getSelfUser().getAvatarUrl();
            ID = client.getSelfUser().getIdLong();

            VERSION = "@buildVersion@";

            client.getPresence().setGame(Game.playing(String.format("Version %s", VERSION)));
            System.out.println("VERSION = " + VERSION);

            client.removeEventListener(this);
        }
    }
}
