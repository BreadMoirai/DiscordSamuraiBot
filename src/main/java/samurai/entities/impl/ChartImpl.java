package samurai.entities.impl;

import samurai.entities.manager.ChartManager;
import samurai.entities.model.Chart;

import java.util.List;

/**
 * @author TonTL
 * @version 4.x - 2/16/2017
 */
public class ChartImpl implements Chart {
    private int chartId;
    private String chartName;
    private List<Integer> maps;
    private boolean isSet;

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public void setMaps(List<Integer> maps) {
        this.maps = maps;
    }

    public void setSet(boolean set) {
        isSet = set;
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
    public List<Integer> getMaps() {
        return maps;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public ChartManager getManager() {
        return null;
    }


}
