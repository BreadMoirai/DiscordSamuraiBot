package samurai.entities.model;

import samurai.entities.manager.ChartManager;

import java.util.List;

/**
 * @author TonTL
 * @version 5.x - 4/1/2017
 */
public interface Chart {
    int getChartId();

    String getChartName();

    List<Integer> getBeatmapIds();

    boolean isSet();

    ChartManager getManager();
}


