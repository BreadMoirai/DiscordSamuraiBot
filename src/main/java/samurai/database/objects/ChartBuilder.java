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
package samurai.database.objects;

import samurai.database.Database;
import samurai.database.dao.ChartDao;

import java.util.List;

public class ChartBuilder {
    private int chartId;
    private String chartName;
    private boolean isSet;
    private List<Integer> beatmapIds;

    public ChartBuilder setChartId(int chartId) {
        this.chartId = chartId;
        return this;
    }

    public ChartBuilder setChartName(String chartName) {
        this.chartName = chartName;
        return this;
    }

    public ChartBuilder setIsSet(boolean isSet) {
        this.isSet = isSet;
        return this;
    }

    public ChartBuilder setBeatmapIds(List<Integer> beatmapIds) {
        this.beatmapIds = beatmapIds;
        return this;
    }

    public ChartBean build() {
        return new ChartBean(chartId, chartName, isSet, beatmapIds);
    }

    public ChartBean create() {
        Database.get().<ChartDao>openDao(ChartDao.class, chartDao -> chartDao.insertChart(chartName, isSet));
        return build();
    }
}