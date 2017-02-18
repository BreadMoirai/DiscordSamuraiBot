package samurai.data;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
public class Chart {
    private int chartId;
    private String chartName;
    private ArrayList<Integer> beatmapIds;

    public Chart(int chartId, String chartName) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.beatmapIds = new ArrayList<>();
    }

    public int getChartId() {
        return chartId;
    }

    public String getChartName() {
        return chartName;
    }

    public boolean addBeatmap(int id) {
        return !beatmapIds.contains(id) && beatmapIds.add(id);
    }

    public ArrayList<Integer> getBeatmapIds() {
        return beatmapIds;
    }
}
