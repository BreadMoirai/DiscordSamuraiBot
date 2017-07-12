/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class BotInfo {


    public final long START_TIME;
    public final String AVATAR;
    public final long ID;
    public final AtomicInteger CALLS;
    public final AtomicInteger SENT;
    public final long SOURCE_GUILD;
    public final String DEFAULT_PREFIX;
    public final long OWNER;
    public final String URL;
    public String VERSION;

    BotInfo(JDA client) {
        START_TIME = Instant.now().getEpochSecond();
        final Config config = ConfigFactory.load();
        SOURCE_GUILD = config.getLong("bot.source_guild");
        DEFAULT_PREFIX = config.getString("bot.prefix");
        OWNER = config.getLong("owner.id");
        URL = config.getString("bot.url");

        CALLS = new AtomicInteger();
        SENT = new AtomicInteger();

        AVATAR = client.getSelfUser().getAvatarUrl();
        ID = client.getSelfUser().getIdLong();

        VERSION = "@buildVersion@";

        client.getPresence().setGame(Game.of(String.format("Version %s", VERSION)));
        System.out.println("VERSION = " + VERSION);
    }

    public String getUserAgent() {
        return String.format("DiscordBot (%s, %s)", URL, VERSION);
    }
}
