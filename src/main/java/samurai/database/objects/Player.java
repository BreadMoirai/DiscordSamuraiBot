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
package samurai.database.objects;

public class Player {
    private long discordId;
    private int osuId;
    private String osuName;
    private int globalRank;
    private int countryRank;
    private double level;
    private double rawPP;
    private double accuracy;
    private int playCount;

    private int countX, countS, countA;
    private int count300, count100, count50;

    private short modes;
    private long lastUpdated;

    public Player(long discordId, int osuId, String osuName, int globalRank, int countryRank, double level, double rawPP, double accuracy, int playCount, int countX, int countS, int countA, int count300, int count100, int count50, short modes, long lastUpdated) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.globalRank = globalRank;
        this.countryRank = countryRank;
        this.level = level;
        this.rawPP = rawPP;
        this.accuracy = accuracy;
        this.playCount = playCount;
        this.countX = countX;
        this.countS = countS;
        this.countA = countA;
        this.count300 = count300;
        this.count100 = count100;
        this.count50 = count50;
        this.modes = modes;
        this.lastUpdated = lastUpdated;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (discordId != player.discordId) return false;
        return osuId == player.osuId;
    }

    @Override
    public int hashCode() {
        int result = (int) (discordId ^ (discordId >>> 32));
        result = 31 * result + osuId;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Player{");
        sb.append("discordId=").append(discordId);
        sb.append(", osuId=").append(osuId);
        sb.append(", osuName='").append(osuName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public PlayerUpdater getUpdater() {
        return new PlayerUpdater(this.getDiscordId());
    }
}
