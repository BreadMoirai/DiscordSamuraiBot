package com.github.breadmoirai.samurai.database.objects;


import com.github.breadmoirai.samurai.database.Database;
import com.github.breadmoirai.samurai.database.dao.PlayerDao;

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
