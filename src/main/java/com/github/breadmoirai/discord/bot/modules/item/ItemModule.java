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
package com.github.breadmoirai.discord.bot.modules.item;

import com.github.breadmoirai.breadbot.framework.BreadBotClientBuilder;
import com.github.breadmoirai.breadbot.framework.CommandModule;
import com.github.breadmoirai.breadbot.util.Arguments;
import com.github.breadmoirai.database.Database;
import com.github.breadmoirai.discord.bot.modules.item.model.Item;
import com.github.breadmoirai.discord.bot.modules.item.model.database.ItemFactory;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ItemModule implements CommandModule {

    public ItemModule() {
        this(false);
    }

    public ItemModule(boolean refreshItems) {
        if (refreshItems) {
            Database.get().useHandle(handle -> {
                handle.execute("DROP TABLE DropRate");
                handle.execute("DROP TABLE ItemCatalog");
            });
        }
    }

    @Override
    public void initialize(BreadBotClientBuilder builder) {
        builder.registerArgumentMapper(Item.class, null, (arg, flags) -> {
            if (arg.isNumeric()) {
                return ItemFactory.getItemById(arg.parseInt());
            } else {
                return ItemFactory.getItemByName(arg.getArgument());
            }
        });
        checkTables();
        builder.addCommands(this.getClass().getPackage().getName() + ".command");
    }

    private void checkTables() {
        if (!Database.hasTable("MemberInventory")) {
            Database.get().useHandle(handle -> handle.execute("" +
                    "CREATE TABLE MemberInventory (\n" +
                    "  UserId  BIGINT NOT NULL,\n" +
                    "  GuildId    BIGINT NOT NULL,\n" +
                    "  SlotId     INT    NOT NULL,\n" +
                    "  ItemId     INT    NOT NULL,\n" +
                    "  Durability INT    NOT NULL,\n" +
                    "  CONSTRAINT Inventory_PK PRIMARY KEY (UserId, GuildId, SlotId)\n" +
                    ")"));
        }

        if (!Database.hasTable("ItemCatalog")) {
            Database.get().useHandle(handle -> {
                handle.execute("" +
                        "CREATE TABLE ItemCatalog (\n" +
                        "  ItemId      INT PRIMARY KEY,\n" +
                        "  Type        VARCHAR(32) NOT NULL,\n" +
                        "  Name        VARCHAR(32) NOT NULL,\n" +
                        "  Rarity      SMALLINT    NOT NULL,\n" +
                        "  Value       INT,\n" +
                        "  Durability  SMALLINT,\n" +
                        "  PropertyA   INT,\n" +
                        "  PropertyB   INT,\n" +
                        "  PropertyC   INT,\n" +
                        "  PropertyD   INT,\n" +
                        "  PropertyE   INT,\n" +
                        "  PropertyF   INT,\n" +
                        "  PropertyG   INT,\n" +
                        "  EmoteId     BIGINT,\n" +
                        "  Description VARCHAR(2000)\n" +
                        ")");
                final PreparedBatch itemBatch = handle.prepareBatch("INSERT INTO ItemCatalog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("ItemCatalog.csv")))) {
                    br.lines()
                            .map(s -> s.split(",", 0))
                            .filter(strings -> strings.length == 15)
                            .forEach(itemData -> {
                                for (int i = 0; i < 15; i++) {
                                    final String value = itemData[i];
                                    switch (i) {
                                        case 0:
                                            if (!Arguments.isNumber(value)) continue;
                                            else itemBatch.bind(i, Integer.parseInt(value));
                                        case 1:
                                        case 2:
                                        case 14:
                                            if (value == null || value.isEmpty())
                                                itemBatch.bindNull(i, Types.VARCHAR);
                                            else itemBatch.bind(i, value);
                                            break;
                                        case 3:
                                        case 5:
                                            if (value == null || value.isEmpty() || !Arguments.isNumber(value)) {
                                                itemBatch.bindNull(i, Types.SMALLINT);
                                            } else itemBatch.bind(i, Short.parseShort(value));
                                            break;
                                        case 13:
                                            if (value == null || value.isEmpty() || !Arguments.isNumber(value))
                                                itemBatch.bindNull(i, Types.BIGINT);
                                            else itemBatch.bind(i, Long.parseLong(value));
                                            break;
                                        case 4:
                                        case 6:
                                        case 7:
                                        case 8:
                                        case 9:
                                        case 10:
                                        case 11:
                                        case 12:
                                            if (value == null || value.isEmpty() || !Arguments.isNumber(value))
                                                itemBatch.bindNull(i, Types.INTEGER);
                                            else itemBatch.bind(i, Integer.parseInt(value));
                                            break;
                                    }
                                }
                                itemBatch.add();
                            });
                    System.out.println("ItemsInserted: " + Arrays.stream(itemBatch.execute()).sum());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (!Database.hasTable("DropRate")) {
            Database.get().useHandle(handle -> {
                handle.execute("" +
                        "CREATE TABLE DropRate (\n" +
                        "  ItemId INT NOT NULL,\n" +
                        "  DropId INT NOT NULL,\n" +
                        "  Weight INT NOT NULL,\n" +
                        "  CONSTRAINT DropRate_PK PRIMARY KEY (ItemId, DropId)\n" +
                        ")");
                final PreparedBatch dropRateBatch = handle.prepareBatch("INSERT INTO DropRate VALUES (?, ?, ?)");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("DropRate.csv")))) {
                    br.lines()
                            .map(s -> s.split(",", 0))
                            .filter(dropData -> dropData.length == 3)
                            .filter(dropData -> Arrays.stream(dropData).allMatch(Arguments::isNumber))
                            .map(dropData -> Arrays.stream(dropData).mapToInt(Integer::parseInt).toArray())
                            .forEach(dropData -> {
                                IntStream.range(0, 3).forEach(i -> dropRateBatch.bind(i, dropData[i]));
                                dropRateBatch.add();
                            });
                    System.out.println("DropValuesInserted: " + Arrays.stream(dropRateBatch.execute()).sum());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}