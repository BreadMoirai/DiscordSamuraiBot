package samurai.entities.model;

import samurai.entities.manager.PlayerManager;

/**
 * @author TonTL
 * @version 4/3/2017
 */
public interface Player {
    long getDiscordId();

    int getOsuId();

    String getOsuName();

    int getRankGlobal();

    int getRankCountry();

    double getRawPP();

    long getLastUpdated();

    PlayerManager getManager();
}
