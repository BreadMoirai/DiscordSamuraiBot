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
package samurai.database.objects;

import samurai.database.Database;
import samurai.database.dao.PlayerDao;

public class PlayerBuilder {
    private long discordId;
    private int osuId;
    private String osuName;
    private int globalRank;
    private int countryRank;
    private double level;
    private double rawPP;
    private double accuracy;
    private int playCount;
    private short modes;
    private long lastUpdated;
    private int countX;
    private int countS;
    private int countA;
    private int count300;
    private int count100;
    private int count50;

    public PlayerBuilder setDiscordId(long discordId) {
        this.discordId = discordId;
        return this;
    }

    public PlayerBuilder setOsuId(int osuId) {
        this.osuId = osuId;
        return this;
    }

    public PlayerBuilder setOsuName(String osuName) {
        this.osuName = osuName;
        return this;
    }

    public PlayerBuilder setGlobalRank(int globalRank) {
        this.globalRank = globalRank;
        return this;
    }

    public PlayerBuilder setCountryRank(int countryRank) {
        this.countryRank = countryRank;
        return this;
    }

    public PlayerBuilder setLevel(double level) {
        this.level = level;
        return this;
    }

    public PlayerBuilder setRawPP(double rawPP) {
        this.rawPP = rawPP;
        return this;
    }

    public PlayerBuilder setAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public PlayerBuilder setPlayCount(int playCount) {
        this.playCount = playCount;
        return this;
    }

    public PlayerBuilder setCountX(int countX) {
        this.countX = countX;
        return this;
    }

    public PlayerBuilder setCountS(int countS) {
        this.countS = countS;
        return this;
    }

    public PlayerBuilder setCountA(int countA) {
        this.countA = countA;
        return this;
    }

    public PlayerBuilder setCount300(int count300) {
        this.count300 = count300;
        return this;
    }

    public PlayerBuilder setCount100(int count100) {
        this.count100 = count100;
        return this;
    }

    public PlayerBuilder setCount50(int count50) {
        this.count50 = count50;
        return this;
    }

    public PlayerBuilder setModes(short modes) {
        this.modes = modes;
        return this;
    }

    public PlayerBuilder setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    /**
     * Do not use this
     * @return a new player
     */
    public PlayerBean build() {
        return new PlayerBean(discordId, osuId, osuName, globalRank, countryRank, level, rawPP, accuracy, playCount, countX, countS, countA, count300, count100, count50, modes, lastUpdated);
    }

    /**
     * creates a player and puts it into the database
     * @return the created Player
     */
    public PlayerBean create() {
        final PlayerBean player = build();
        Database.get().<PlayerDao>openDao(PlayerDao.class, dao -> dao.insertPlayer(player));
        return player;
    }
}