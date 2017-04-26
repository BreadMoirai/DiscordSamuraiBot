package samurai.entities.impl;

import samurai.entities.model.Player;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
public class PlayerImpl implements Player {
    private long discordId;
    private int osuId;
    private String osuName;
    private int rankGlobal;
    private int rankCountry;
    private long lastUpdated;
    private double rawPP;

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public void setOsuId(int osuId) {
        this.osuId = osuId;
    }

    public void setOsuName(String osuName) {
        this.osuName = osuName;
    }

    public void setRankGlobal(int rankGlobal) {
        this.rankGlobal = rankGlobal;
    }

    public void setRankCountry(int rankCountry) {
        this.rankCountry = rankCountry;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setRawPP(double rawPP) {
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
    public int getRankGlobal() {
        return rankGlobal;
    }

    @Override
    public int getRankCountry() {
        return rankCountry;
    }

    @Override
    public double getRawPP() {
        return rawPP;
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Player{");
        sb.append("discordId=").append(discordId);
        sb.append(", osuId=").append(osuId);
        sb.append(", osuName='").append(osuName).append('\'');
        sb.append(", rankGlobal=").append(rankGlobal);
        sb.append(", rankCountry=").append(rankCountry);
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
}
