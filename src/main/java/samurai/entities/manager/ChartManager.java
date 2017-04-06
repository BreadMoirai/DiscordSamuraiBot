package samurai.entities.manager;

/**
 * @author TonTL
 * @version 4/3/2017
 */
public interface ChartManager {
    boolean addMapSet(int mapSetId);

    boolean removeMapSet(int mapSetId);

    boolean changeName(String newName);
}
