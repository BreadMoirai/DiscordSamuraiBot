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

public class PlayerBean {
    private long userId;
    private int osuId;
    private String osuName;
    private int globalRank;
    private int countryRank;
    private double rawPP;
    private double accuracy;
    private int playCount;
    private long lastUpdated;

    PlayerBean(long userId, int osuId, String osuName, int globalRank, int countryRank, double rawPP, double accuracy, int playCount, long lastUpdated) {
        this.userId = userId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.globalRank = globalRank;
        this.countryRank = countryRank;
        this.rawPP = rawPP;
        this.accuracy = accuracy;
        this.playCount = playCount;
        this.lastUpdated = lastUpdated;
    }

    public long getUserId() {
        return userId;
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

    public double getRawPP() {
        return rawPP;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getPlayCount() {
        return playCount;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerBean player = (PlayerBean) o;

        if (userId != player.userId) return false;
        return osuId == player.osuId;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + osuId;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlayerBean{");
        sb.append("userId=").append(userId);
        sb.append(", osuId=").append(osuId);
        sb.append(", osuName='").append(osuName).append('\'');
        sb.append(", globalRank=").append(globalRank);
        sb.append(", countryRank=").append(countryRank);
        sb.append(", rawPP=").append(rawPP);
        sb.append(", accuracy=").append(accuracy);
        sb.append(", playCount=").append(playCount);
        sb.append(", lastUpdated=").append(lastUpdated);
        sb.append('}');
        return sb.toString();
    }
}
