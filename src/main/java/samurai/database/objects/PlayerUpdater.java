package samurai.database.objects;

import samurai.database.Database;
import samurai.database.dao.PlayerDao;
import samurai.osu.SessionStats;

public class PlayerUpdater {

    private long discordId;

    public PlayerUpdater(long discordId) {
        this.discordId = discordId;
    }

    public SessionStats updateTo(Player newPlayer) {
        Database.get().<PlayerDao>openDao(PlayerDao.class, playerDao -> playerDao.updatePlayer(discordId, newPlayer));
        return null;
    }

    public void destroy() {
        Database.get().<PlayerDao>openDao(PlayerDao.class, playerDao -> playerDao.destroyPlayer(discordId));
    }

}
