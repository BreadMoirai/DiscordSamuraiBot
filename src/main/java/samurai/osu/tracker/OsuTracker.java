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
package samurai.osu.tracker;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.database.objects.Player;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;

/**
 * @author TonTL
 * @version 5.x - 3/12/2017
 */
public class OsuTracker {

    private static final CopyOnWriteArrayList<OsuSession> tracking;
    private static final ScheduledExecutorService service;

    static {
        tracking = new CopyOnWriteArrayList<>();
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(OsuTracker::dotheThing, 1, 30, TimeUnit.SECONDS);
    }

    private static void dotheThing() {
        tracking.forEach(OsuSession::update);
    }

    public static boolean isTracking(long discordUserId) {
        LongPredicate idMatch = value -> discordUserId == value;
        return tracking.stream().map(OsuSession::getPlayer).mapToLong(Player::getDiscordId).anyMatch(idMatch);
    }

    public static Optional<OsuSession> retrieveSession(long discordUserId) {
        return tracking.stream().filter(osuSession -> osuSession.getPlayer().getDiscordId() == discordUserId).findAny();
    }


    public static boolean register(Player player, TextChannel channel) {
        final OsuSession osuSession = new OsuSession(player);
        osuSession.addChannel(channel);
        return tracking.add(osuSession);
    }

    public static void close() {
        tracking.clear();
        service.shutdown();
        System.out.println("Osu Tracker has shut down");
    }
}
