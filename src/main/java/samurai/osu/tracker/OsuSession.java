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
package samurai.osu.tracker;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.database.objects.Player;
import samurai.osu.OsuAPI;
import samurai.osu.enums.GameMode;
import samurai.osu.model.Score;
import samurai.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class OsuSession {

    private final Player playerStart;
    private final List<TextChannel> channels;
    private final ScoreCache scoresFound;

    OsuSession(Player playerStart) {
        this.playerStart = playerStart;
        channels = new ArrayList<>(5);
        scoresFound = new ScoreCache();
    }

    public Player getPlayer() {
        return playerStart;
    }

    public boolean addChannel(TextChannel channel) {
        channel.sendMessage("Kakugo!").queue();
        return !channels.contains(channel) && channels.add(channel);
    }

    void update() {
        final List<Score> userRecent = OsuAPI.getUserRecent(playerStart.getOsuName(), playerStart.getOsuId(), GameMode.STANDARD, 15);
        channels.forEach(textChannel -> textChannel.sendMessage("Found " + userRecent.size() + " Recent Scores").queue());
        scoresFound.addScores(userRecent)
                .forEach(score -> channels.forEach(textChannel -> textChannel.sendMessage(MessageUtil.build(score)).queue()));
    }



}
