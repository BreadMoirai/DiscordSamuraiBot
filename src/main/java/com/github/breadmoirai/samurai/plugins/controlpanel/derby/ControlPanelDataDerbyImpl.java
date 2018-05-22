/*
 *     Copyright 2017-2018 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.breadmoirai.samurai.plugins.controlpanel.derby;

import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanel;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelBuilder;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelData;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelOption;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelOptionEmoji;
import com.github.breadmoirai.samurai.plugins.controlpanel.ControlPanelOptionEmote;
import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ControlPanelDataDerbyImpl extends JdbiExtension implements ControlPanelData {

    public ControlPanelDataDerbyImpl(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("ControlPanels")) {
            execute("CREATE TABLE ControlPanels (\n" +
                            "  Id            INT     NOT NULL GENERATED ALWAYS AS IDENTITY" +
                            "                        (START WITH 1, INCREMENT BY 1),\n" +
                            "  GuildId       BIGINT  NOT NULL,\n" +
                            "  ChannelId     BIGINT  NOT NULL,\n" +
                            "  MessageId     BIGINT  NOT NULL,\n" +
                            "  PanelType     CHAR(1) NOT NULL,\n" +
                            "  CONSTRAINT CP_PK PRIMARY KEY (Id),\n" +
                            "  CONSTRAINT CP_TYPE_SPACE CHECK (PanelType IN ('R', 'C', 'A'))\n" +
                            ")");
            execute("CREATE TABLE ControlPanelEmojis (\n" +
                            "  Id            INT         NOT NULL,\n" +
                            "  Emoji         VARCHAR(14) NOT NULL,\n" +
                            "  Target        BIGINT      NOT NULL,\n" +
                            "  CONSTRAINT CP_Emojis_PK PRIMARY KEY (Id, Emoji),\n" +
                            "  CONSTRAINT CP_Emojis_FK FOREIGN KEY (Id) REFERENCES ControlPanels(Id)\n" +
                            ")");
            execute("CREATE TABLE ControlPanelEmotes (\n" +
                            "  Id            INT    NOT NULL,\n" +
                            "  Emote         BIGINT NOT NULL,\n" +
                            "  Target        BIGINT NOT NULL,\n" +
                            "  CONSTRAINT CP_Emotes_PK PRIMARY KEY (Id, Emote),\n" +
                            "  CONSTRAINT CP_Emotes_FK FOREIGN KEY (Id) REFERENCES ControlPanels(Id)\n" +
                            ")");
        }
    }

    @Override
    public List<ControlPanel> getControlPanels() {
        return withHandle(handle -> {
            final TIntObjectMap<ControlPanelBuilder> cpMap;
            cpMap = handle.createQuery("SELECT * FROM ControlPanels")
                    .map(new ControlPanelRowMapper())
                    .collect(Collector.of(TIntObjectHashMap::new,
                                          (map, cp) -> map.put(cp.getId(), cp),
                                          (map1, map2) -> {
                                              map1.putAll(map2);
                                              return map1;
                                          }));
            handle.createQuery("SELECT * FROM ControlPanelEmojis")
                    .map(new ControlPanelEmojiMapper())
                    .forEach(option -> cpMap.get(option.getPanelId()).addOption(option));
            handle.createQuery("SELECT * FROM ControlPanelEmotes")
                    .map(new ControlPanelEmoteMapper())
                    .forEach(option -> cpMap.get(option.getPanelId()).addOption(option));
            return cpMap.valueCollection()
                    .parallelStream()
                    .map(ControlPanelBuilder::build)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public ControlPanel getControlPanel(int id) {
        return withHandle(handle -> {
            final Optional<ControlPanelBuilder> first = handle.createQuery("SELECT * FROM ControlPanels WHERE Id = ?")
                    .bind(0, id)
                    .map(new ControlPanelRowMapper())
                    .findFirst();
            if (!first.isPresent()) {
                return null;
            }
            final ControlPanelBuilder builder = first.get();
            handle.createQuery("SELECT * FROM ControlPanelEmojis WHERE Id = ?")
                    .bind(0, id)
                    .map(new ControlPanelEmojiMapper())
                    .forEach(builder::addOption);
            handle.createQuery("SELECT * FROM ControlPanelEmotes WHERE Id = ?")
                    .bind(0, id)
                    .map(new ControlPanelEmoteMapper())
                    .forEach(builder::addOption);
            return builder.build();
        });
    }

    @Override
    public ControlPanel createControlPanel(long guildId, long channelId, long messageId, char type,
                                           List<ControlPanelOption> options) {
        final Integer id = withHandle(handle -> handle
                .createUpdate("INSERT INTO ControlPanels (GuildId, ChannelId, MessageId, PanelType)" +
                                      " VALUES (?, ?, ?, ?)")
                .bind(0, guildId)
                .bind(1, channelId)
                .bind(2, messageId)
                .bind(3, type)
                .executeAndReturnGeneratedKeys("ID")
                .mapTo(Integer.class)
                .findOnly());
        final Map<Boolean, List<ControlPanelOption>> optionTypeMap = options.stream()
                .collect(Collectors.partitioningBy(ControlPanelOptionEmoji.class::isInstance));
        if (!optionTypeMap.get(true).isEmpty())
            useHandle(handle -> {
                final PreparedBatch batch = handle
                        .prepareBatch("INSERT INTO ControlPanelEmojis VALUES (?, ?, ?)");
                for (final ControlPanelOption option : optionTypeMap.get(true)) {
                    final ControlPanelOptionEmoji emoji = (ControlPanelOptionEmoji) option;
                    batch.add(id, emoji.getEmoji(), emoji.getTarget());
                }
                batch.execute();
            });
        if (!optionTypeMap.get(false).isEmpty())
            useHandle(handle -> {
                final PreparedBatch batch = handle.prepareBatch("INSERT INTO ControlPanelEmotes VALUES (?, ?, ?)");
                for (final ControlPanelOption controlPanelOption : optionTypeMap.get(false)) {
                    final ControlPanelOptionEmote emote = (ControlPanelOptionEmote) controlPanelOption;
                    batch.add(id, emote.getEmote(), emote.getTarget());
                }
                batch.execute();
            });
        return getControlPanel(id);
    }

    @Override
    public void deleteControlPanel(int controlPanelId) {
        execute("DELETE FROM ControlPanelEmojis WHERE Id = ?", controlPanelId);
        execute("DELETE FROM ControlPanelEmotes WHERE Id = ?", controlPanelId);
        execute("DELETE FROM ControlPanels WHERE Id = ?", controlPanelId);
    }

}
