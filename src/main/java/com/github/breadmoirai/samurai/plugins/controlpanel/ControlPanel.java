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

import java.util.List;

public class ControlPanel {

    private final int id;
    private final long guildId, channelId, messageId;
    private final List<ControlPanelOption> options;
    private final ControlPanelType type;

    public ControlPanel(int id, long guildId, long channelId, long messageId,
                        List<ControlPanelOption> options,
                        ControlPanelType type) {
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.options = options;
        this.type = type;
    }

}
