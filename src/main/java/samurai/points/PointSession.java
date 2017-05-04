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
import samurai.database.Database;
import samurai.database.dao.PointDao;

public class PointSession {
    long userId;
    long guildId;
    long points;
    OnlineStatus status;

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

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public PointSession setStatus(OnlineStatus status) {
        this.status = status;
        return this;
    }

    public void destroy() {
        Database.get().<PointDao>openDao(PointDao.class, pointDao -> pointDao.deleteUser(userId, guildId));
    }

    public void commit() {
        Database.get().<PointDao>openDao(PointDao.class, pointDao -> pointDao.update(userId, guildId, points));
    }

}
