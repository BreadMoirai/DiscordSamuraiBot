package samurai.entities.model;

/**
 * @author TonTL
 * @version 4/3/2017
 */
public interface Player {
    long getDiscordId();

    int getOsuId();

    String getOsuName();

    int getRankG();

    int getRankC();

    long getLastUpdated();
}
