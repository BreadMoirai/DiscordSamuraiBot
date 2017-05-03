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

import java.util.List;

public class Chart {
    private int chartId;
    private String chartName;
    private boolean isSet;
    private List<Integer> beatmapIds;


    Chart(int chartId, String chartName, boolean isSet, List<Integer> beatmapIds) {
        this.chartId = chartId;
        this.chartName = chartName;
        this.isSet = isSet;
        this.beatmapIds = beatmapIds;
    }

    public int getChartId() {
        return chartId;
    }

    public String getChartName() {
        return chartName;
    }

    public boolean isSet() {
        return isSet;
    }

    public List<Integer> getBeatmapIds() {
        return beatmapIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chart chartBean = (Chart) o;

        if (chartId != chartBean.chartId) return false;
        if (isSet != chartBean.isSet) return false;
        if (!chartName.equals(chartBean.chartName)) return false;
        return beatmapIds != null ? beatmapIds.equals(chartBean.beatmapIds) : chartBean.beatmapIds == null;
    }

    @Override
    public int hashCode() {
        int result = chartId;
        result = 31 * result + chartName.hashCode();
        result = 31 * result + (isSet ? 1 : 0);
        result = 31 * result + (beatmapIds != null ? beatmapIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChartBean{");
        sb.append("chartId=").append(chartId);
        sb.append(", chartName='").append(chartName).append('\'');
        sb.append(", isSet=").append(isSet);
        sb.append(", beatmapIds=").append(beatmapIds);
        sb.append('}');
        return sb.toString();
    }
}
