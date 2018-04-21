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

import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.samurai.plugins.controlpanel.derby.ControlPanelDataDerbyImpl;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;

public class ControlPanelPlugin implements CommandPlugin {

    private ControlPanelDataDerbyImpl data;

    @Override
    public void initialize(BreadBotBuilder builder) {
        builder.addCommand(this);
    }

    @Override
    public void onBreadReady(BreadBot client) {
        final DerbyDatabase database = client.getPluginOrThrow(DerbyDatabase.class);
        this.data = database.getExtension(ControlPanelDataDerbyImpl::new);
    }

}