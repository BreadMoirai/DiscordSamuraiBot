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

import net.dv8tion.jda.core.entities.MessageReaction;

public class ControlPanelOptionEmoji extends ControlPanelOption {

    private final String emoji;

    public ControlPanelOptionEmoji(int panelId, String emoji, long target) {
        super(panelId, target);
        this.emoji = emoji;
    }

    @Override
    public boolean test(MessageReaction reaction) {
        return (!reaction.getReactionEmote().isEmote()) &&
                reaction.getReactionEmote().getName().equals(emoji);
    }

    public String getEmoji() {
        return emoji;
    }
}
