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
    private int countX;
    private int countS;
    private int countA;
    private int count300;
    private int count100;
    private int count50;
    private short modes;
    private long lastUpdated;

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public void setOsuId(int osuId) {
        this.osuId = osuId;
    }

    public void setOsuName(String osuName) {
        this.osuName = osuName;
    }

    public void setGlobalRank(int globalRank) {
        this.globalRank = globalRank;
    }

    public void setCountryRank(int countryRank) {
        this.countryRank = countryRank;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public void setRawPP(double rawPP) {
        this.rawPP = rawPP;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public void setCountX(int countX) {
        this.countX = countX;
    }

    public void setCountS(int countS) {
        this.countS = countS;
    }

    public void setCountA(int countA) {
        this.countA = countA;
    }

    public void setCount300(int count300) {
        this.count300 = count300;
    }

    public void setCount100(int count100) {
        this.count100 = count100;
    }

    public void setCount50(int count50) {
        this.count50 = count50;
    }

    public void setModes(short modes) {
        this.modes = modes;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public PlayerBuilder putDiscordId(long discordId) {
        this.discordId = discordId;
        return this;
    }

    public PlayerBuilder putOsuId(int osuId) {
        this.osuId = osuId;
        return this;
    }

    public PlayerBuilder putOsuName(String osuName) {
        this.osuName = osuName;
        return this;
    }

    public PlayerBuilder putGlobalRank(int globalRank) {
        this.globalRank = globalRank;
        return this;
    }

    public PlayerBuilder putCountryRank(int countryRank) {
        this.countryRank = countryRank;
        return this;
    }

    public PlayerBuilder putLevel(double level) {
        this.level = level;
        return this;
    }

    public PlayerBuilder putRawPP(double rawPP) {
        this.rawPP = rawPP;
        return this;
    }

    public PlayerBuilder putAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public PlayerBuilder putPlayCount(int playCount) {
        this.playCount = playCount;
        return this;
    }

    public PlayerBuilder putCountX(int countX) {
        this.countX = countX;
        return this;
    }

    public PlayerBuilder putCountS(int countS) {
        this.countS = countS;
        return this;
    }

    public PlayerBuilder putCountA(int countA) {
        this.countA = countA;
        return this;
    }

    public PlayerBuilder putCount300(int count300) {
        this.count300 = count300;
        return this;
    }

    public PlayerBuilder putCount100(int count100) {
        this.count100 = count100;
        return this;
    }

    public PlayerBuilder putCount50(int count50) {
        this.count50 = count50;
        return this;
    }

    public PlayerBuilder putModes(short modes) {
        this.modes = modes;
        return this;
    }

    public PlayerBuilder putLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    /**
     * Do not use this
     * @return a new player
     */
    public Player build() {
        return new Player(discordId, osuId, osuName, globalRank, countryRank, level, rawPP, accuracy, playCount, countX, countS, countA, count300, count100, count50, modes, lastUpdated);
    }

    /**
     * creates a player and puts it into the database
     * @return the created Player
     */
    public Player create() {
        final Player player = build();
        Database.get().<PlayerDao>openDao(PlayerDao.class, dao -> dao.insertPlayer(player));
        return player;
    }

    public long getDiscordId() {
        return discordId;
    }

    public int getOsuId() {
        return osuId;
    }

    public String getOsuName() {
        return osuName;
    }

    public int getGlobalRank() {
        return globalRank;
    }

    public int getCountryRank() {
        return countryRank;
    }

    public double getLevel() {
        return level;
    }

    public double getRawPP() {
        return rawPP;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getPlayCount() {
        return playCount;
    }

    public int getCountX() {
        return countX;
    }

    public int getCountS() {
        return countS;
    }

    public int getCountA() {
        return countA;
    }

    public int getCount300() {
        return count300;
    }

    public int getCount100() {
        return count100;
    }

    public int getCount50() {
        return count50;
    }

    public short getModes() {
        return modes;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}