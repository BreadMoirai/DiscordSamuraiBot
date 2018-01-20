/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.github.breadmoirai.discord.bot.modules.item.model.database;

import com.github.breadmoirai.discord.bot.framework.core.Response;
import com.github.breadmoirai.discord.bot.modules.item.model.Item;
import com.github.breadmoirai.discord.bot.modules.item.responses.RedPacketDrop;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DropTable implements RowMapper<int[]>, Serializable {
    private int[] drops;
    private int[] weights;

    public DropTable() {}

    public DropTable(List<int[]> dropsTable) {
        drops = dropsTable.stream().mapToInt(value -> value[0]).toArray();
        weights = dropsTable.stream().mapToInt(value -> value[1]).toArray();
    }

    public Item getDrop() {
        final int sum = Arrays.stream(weights).sum();
        final int rand = ThreadLocalRandom.current().nextInt(sum);
        int current = 0;
        for (int i = 0; i < weights.length; i++) {
            current += weights[i];
            if (rand < current) {
                return ItemFactory.getItemById(drops[i]);
            }
        }
        return null;
    }

    public int getDropId() {
        final int sum = Arrays.stream(weights).sum();
        final int rand = ThreadLocalRandom.current().nextInt(sum);
        int current = 0;
        for (int i = 0; i < weights.length; i++) {
            current += weights[i];
            if (rand < current) {
                return drops[i];
            }
        }
        return 0;
    }

    @Override
    public int[] map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new int[]{rs.getInt("DropId"), rs.getInt("Weight")};
    }

    public Response createDrop(int count, Duration duration) {
        final TIntIntHashMap map = new TIntIntHashMap();
        for (int i = 0; i < count; i++) {
            map.adjustOrPutValue(getDropId(), 1, 1);
        }
        final int size = map.size();
        final int[] drops = new int[size * 2];
        final TIntIntIterator itr = map.iterator();
        final TIntArrayList dropQueueList = new TIntArrayList();
        int i = 0;
        while (itr.hasNext()) {
            itr.advance();
            final int itemId = itr.key();
            drops[i++] = itemId;
            final int itemCount = itr.value();
            drops[i++] = itemCount;
            for (int j = 0; j < itemCount; j++) {
                dropQueueList.add(itemId);
            }
        }
        dropQueueList.shuffle(new Random());
        return new RedPacketDrop(duration, drops, dropQueueList.toArray());
    }
}
