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

package com.github.breadmoirai.samurai.plugins.personal;

import com.github.breadmoirai.breadbot.framework.CommandPlugin;
import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;

public class BreadMoiraiSamuraiPlugin implements CommandPlugin, net.dv8tion.jda.core.hooks.EventListener {

    private Emote check, xmark;
    private Emote checkEmote;

    @Override
    public void initialize(BreadBotBuilder builder) {
        builder.addCommand(ColorRoleCommand::new);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            check = event.getJDA().getEmoteById(409928211258933249L);
            xmark = event.getJDA().getEmoteById(409928210978045955L);
        } else if (event instanceof GuildMemberJoinEvent) {
            final GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
            if (e.getGuild().getIdLong() == 233097800722808832L) {
                e.getGuild().getController().addSingleRoleToMember(e.getMember(), e.getGuild().getRoleById(267924616574533634L)).queue();
            }
        }
    }

    public Emote getCheckEmote() {
        return checkEmote;
    }

    public Emote getXMarkEmote() {
        return xmark;
    }
}
