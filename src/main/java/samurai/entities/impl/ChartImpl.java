package samurai.entities.impl;

import samurai.entities.manager.ChartManager;
import samurai.entities.manager.impl.ChartManagerImpl;
import samurai.entities.model.Chart;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
public class ChartImpl implements Chart {

    private int chartId;
    private String chartName;
    private boolean isSet;
    private ArrayList<Integer> beatmapIds;
    private ChartManager manager;

    public ChartImpl(int chartId, String chartName, boolean isSet) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.isSet = isSet;
        this.beatmapIds = new ArrayList<>(6);
        this.manager = null;
    }

    @Override
    public int getChartId() {
        return chartId;
    }

    @Override
    public String getChartName() {
        return chartName;
    }

    @Override
    public ArrayList<Integer> getBeatmapIds() {
        return beatmapIds;
    }

    public boolean addMapId(int i) {
        return beatmapIds.add(i);
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public ChartManager getManager() {
        return manager == null ? (manager = new ChartManagerImpl(this)) : manager;
    }

    public void setName(String name) {
        this.chartName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartImpl chart = (ChartImpl) o;

        return chartId == chart.chartId && chartName.equals(chart.chartName);
    }

    @Override
    public int hashCode() {
        int result = chartId;
        result = 31 * result + (chartName != null ? chartName.hashCode() : 0);
        return result;
    }
}
