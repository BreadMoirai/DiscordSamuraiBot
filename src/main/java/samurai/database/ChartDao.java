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
package com.github.breadmoirai.samurai.database.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import com.github.breadmoirai.samurai.database.objects.Chart;
import com.github.breadmoirai.samurai.database.objects.ChartBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ChartDao {

    @SqlUpdate("INSERT INTO Chart VALUES (?, ?)")
    void insertChart(String name, boolean isSet);

    @SqlQuery("SELECT * FROM Chart WHERE ChartId = ?")
    @RegisterBeanMapper(ChartBuilder.class)
    ChartBuilder selectChart(int chartId);

    @SqlQuery("SELECT MapSetId FROM ChartMap WHERE ChartId = ?")
    List<Integer> selectChartMaps(int chartId);

    default Chart getChart(int chartId) {
        return selectChart(chartId).setBeatmapIds(selectChartMaps(chartId)).build();
    }

    @SqlQuery("SELECT Chart.* FROM GuildChart JOIN Chart ON Chart.ChartID = GuildChart.ChartID WHERE Chart.GuildID = ?")
    List<ChartBuilder> selectGuildCharts(long guildId);

    default List<Chart> getGuildCharts(long guildId) {
        return selectGuildCharts(guildId).stream().map(ChartBuilder::build).collect(Collectors.toCollection(ArrayList::new));
    }
}
