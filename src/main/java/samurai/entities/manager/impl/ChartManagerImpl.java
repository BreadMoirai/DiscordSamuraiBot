/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.entities.manager.impl;

import samurai.database.Database;
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
        database = Database.getDatabase();
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
