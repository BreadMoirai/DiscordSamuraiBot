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

import com.github.breadmoirai.breadbot.framework.AbstractCommandPlugin;
import com.github.breadmoirai.breadbot.framework.BreadBot;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiterPlugin;
import com.github.breadmoirai.samurai.plugins.controlpanel.derby.ControlPanelDataDerbyImpl;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class ControlPanelPlugin extends AbstractCommandPlugin implements EventListener {

    private ControlPanelDataDerbyImpl data;
    private EventWaiter waiter;

    public void i(BreadBotBuilder builder) {
        builder.addCommand(ControlPanelCommand::new);
    }

    public void r(BreadBot client) {
        final DerbyDatabase database = client.getPluginOrThrow(DerbyDatabase.class);
        this.data = database.getExtension(ControlPanelDataDerbyImpl::new);
        this.waiter = client.getPluginOrThrow(EventWaiterPlugin.class).getEventWaiter();
    }

    @Override
    protected void initialize() {
        addCommand(ControlPanelCommand::new);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            for (ControlPanel controlPanel : data.getControlPanels()) {
                validatePanel(event.getJDA(), controlPanel);
            }
        }
    }

    /**
     * on start up
     */
    private void validatePanel(JDA jda, ControlPanel controlPanel) {
        final int id = controlPanel.getId();
        final long guildId = controlPanel.getGuildId();
        final long channelId = controlPanel.getChannelId();
        final long messageId = controlPanel.getMessageId();
        final Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            data.deleteControlPanel(id);
            return;
        }
        final TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            data.deleteControlPanel(id);
            return;
        }

        channel.getMessageById(messageId).queue(message -> waiter.waitForReaction()
                .on(message)
                .action(event -> controlPanel
                        .getOptions()
                        .stream()
                        .filter(option -> option.test(event.getReaction()))
                        .findFirst()
                        .ifPresent(option -> {
                            boolean isAdd = event instanceof MessageReactionAddEvent;
                            controlPanel.getType().operate(option.getTarget(), event.getMember(), isAdd);
                        })));
    }
}