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

import net.dv8tion.jda.core.entities.Message;
import samurai.osu.model.Score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author TonTL
 * @version 5.x - 3/19/2017
 */
public class ScoreCache {

    private HashSet<Score> scores;

    public ScoreCache() {
        this.scores = new HashSet<>();
    }

    public List<Score> addScores(List<Score> userRecent) {
        ArrayList<Score> newScores = new ArrayList<>();
        for (Score s : userRecent) {
            if (scores.add(s)) {
                newScores.add(s);
            }
        }
        return newScores;
    }

    public Message getSessionStats() {
        return null;
    }
}
