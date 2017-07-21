/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.points;


import net.breadmoirai.sbf.database.Database;

public class PointSession {
    private long userId;
    private long guildId;
    private double points;
    private double exp;
    private long lastMessageSent;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public long getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(long lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    public void commit() {
        Database.get().useExtension(PointDao.class, pointDao -> pointDao.update(userId, guildId, points, exp));
    }

    public PointSession offsetPoints(double offset) {
        points += offset;
        return this;
    }

    public PointSession offsetExp(double exp) {
        this.exp += exp;
        return this;
    }

    public double getLevel() {
        return LevelStatus.calculateLevel(getExp());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointSession that = (PointSession) o;

        return userId == that.userId && guildId == that.guildId;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (guildId ^ (guildId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PointSession{");
        sb.append("userId=").append(userId);
        sb.append(", guildId=").append(guildId);
        sb.append(", points=").append(points);
        sb.append(", exp=").append(exp);
        sb.append('}');
        return sb.toString();
    }
}
