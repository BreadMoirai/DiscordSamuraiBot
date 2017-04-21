package samurai.entities.manager.impl;

import samurai.database.DatabaseSingleton;
import samurai.database.SDatabase;
import samurai.entities.manager.ChartManager;
import samurai.entities.model.Chart;
import samurai.entities.impl.ChartImpl;

/**
 * @author TonTL
 * @version 5.x - 4/1/2017
 */
public class ChartManagerImpl implements ChartManager {

    private ChartImpl chart;
    private SDatabase database;

    public ChartManagerImpl(ChartImpl chart) {
        this.chart = chart;
        database = DatabaseSingleton.getDatabase();
    }

    @Override
    public boolean addMapSet(int mapSetId) {
        chart.addMapId(mapSetId);
        return database.putChartMap(chart.getChartId(), mapSetId);
    }

    @Override
    public boolean removeMapSet(int mapSetId) {
        return false;
    }

    @Override
    public boolean changeName(String newName) {
        chart.setName(newName);
        return database.updateChart(chart.getChartId(), newName);
    }

    public Chart getChart() {
        return chart;
    }
}
