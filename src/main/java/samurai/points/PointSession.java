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
package samurai.points;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import samurai.database.Database;
import samurai.database.dao.PointDao;

public class PointSession {
    private long discordId;
    private long guildId;
    private double points;
    private OnlineStatus status;
    private Member member;
    private long lastMessageSent;

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
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

    public OnlineStatus getStatus() {
        return status;
    }

    public PointSession setStatus(OnlineStatus status) {
        this.status = status;
        return this;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public long getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(long lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    /**
     * deletes this member from the database.
     * use with caution
     */
    public void delete() {
        Database.get().<PointDao>openDao(PointDao.class, pointDao -> pointDao.deleteUser(discordId, guildId));
    }

    public void commit() {
        Database.get().<PointDao>openDao(PointDao.class, pointDao -> pointDao.update(discordId, guildId, points));
    }

    public PointSession offsetPoints(double offset) {
        points += offset;
        return this;
    }

    public double getLevel() {
        return 0.0;
        //todo
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointSession that = (PointSession) o;

        if (discordId != that.discordId) return false;
        return guildId == that.guildId;
    }

    @Override
    public int hashCode() {
        int result = (int) (discordId ^ (discordId >>> 32));
        result = 31 * result + (int) (guildId ^ (guildId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PointSession{");
        sb.append("discordId=").append(discordId);
        sb.append(", guildId=").append(guildId);
        sb.append(", points=").append(points);
        sb.append(", member=").append(member);
        sb.append('}');
        return sb.toString();
    }
}
