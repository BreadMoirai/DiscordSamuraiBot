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

package com.github.breadmoirai.samurai.plugins.controlpanel;

import java.util.ArrayList;
import java.util.List;

public class ControlPanelBuilder {

    private int id;
    private long guildId;
    private long channelId;
    private long messageId;
    private List<ControlPanelOption> options = new ArrayList<>();
    private ControlPanelType type;

    public ControlPanelBuilder setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public ControlPanelBuilder setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    public ControlPanelBuilder setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public ControlPanelBuilder addOption(String emoji, long target) {
        options.add(new ControlPanelOptionEmoji(id, emoji, target));
        return this;
    }

    public ControlPanelBuilder addOption(long emote, long target) {
        options.add(new ControlPanelOptionEmote(id, emote, target));
        return this;
    }

    public ControlPanelBuilder addOption(ControlPanelOption option) {
        options.add(option);
        return this;
    }

    public ControlPanelBuilder setType(ControlPanelType type) {
        this.type = type;
        return this;
    }

    public int getId() {
        return id;
    }

    public ControlPanelBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ControlPanel build() {
        return new ControlPanel(id, guildId, channelId, messageId, options, type);
    }
}