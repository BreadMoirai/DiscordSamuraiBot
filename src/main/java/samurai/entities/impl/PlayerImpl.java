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
package samurai.entities.impl;

import samurai.entities.model.Player;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
public class PlayerImpl implements Player {
    private final long discordId;
    private final int osuId;
    private final String osuName;
    private final int rankG;
    private final int rankC;
    private final long lastUpdated;
    private final int rawPP;

    public PlayerImpl(long discordId, int osuId, String osuName, int g, int c, long lastUpdated, int rawPP) {
        this.discordId = discordId;
        this.osuId = osuId;
        this.osuName = osuName;
        this.rankG = g;
        this.rankC = c;
        this.lastUpdated = lastUpdated;
        this.rawPP = rawPP;
    }

    @Override
    public long getDiscordId() {
        return discordId;
    }

    @Override
    public int getOsuId() {
        return osuId;
    }

    @Override
    public String getOsuName() {
        return osuName;
    }

    @Override
    public int getRankG() {
        return rankG;
    }

    @Override
    public int getRankC() {
        return rankC;
    }

    @Override
    public int getRawPP() {
        return rawPP;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Player{");
        sb.append("discordId=").append(discordId);
        sb.append(", osuId=").append(osuId);
        sb.append(", osuName='").append(osuName).append('\'');
        sb.append(", rankG=").append(rankG);
        sb.append(", rankC=").append(rankC);
        sb.append(", lastUpdated=").append(lastUpdated);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerImpl player = (PlayerImpl) o;

        if (discordId != player.discordId) return false;
        if (osuId != player.osuId) return false;
        if (lastUpdated != player.lastUpdated) return false;
        return osuName != null ? osuName.equals(player.osuName) : player.osuName == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (discordId ^ (discordId >>> 32));
        result = 31 * result + osuId;
        result = 31 * result + (osuName != null ? osuName.hashCode() : 0);
        result = 31 * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
        return result;
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }
}
