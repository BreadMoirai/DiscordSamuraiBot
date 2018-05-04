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

import com.github.breadmoirai.breadbot.framework.annotation.command.Command;
import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandArgumentList;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.framework.parameter.CommandArgument;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ControlPanelCommand {

    @Inject
    public ControlPanelPlugin plugin;

    @MainCommand
    public void createCP(CommandEvent event) {
        final CommandArgumentList arguments = event.getArguments();
        final int size = arguments.size();
        if (size % 2 != 0 || size < 4) {
            event.send(help(event));
            return;
        }
        final Optional<TextChannel> channelOpt = arguments.get(0).findTextChannel();
        if (!channelOpt.isPresent()) {
            event.send("`" + arguments.get(0).getArgument() + "` is not a valid text channel.");
            return;
        }
        final TextChannel channel = channelOpt.get();
        final CommandArgument messageArg = arguments.get(1);
        if (!messageArg.isLong()) {
            event.send("`" + messageArg.getArgument() + "` is not a valid message.");
            return;
        }
        List<ControlPanelOption> options = new LinkedList<>();
        boolean isTargetChannel = arguments.get(3).findTextChannel().isPresent();
        for (int i = 2; i < arguments.size(); i += 2) {
            final CommandArgument reaction = arguments.get(i);
            final CommandArgument target = arguments.get(i + 1);
            if (isTargetChannel) {
                final Optional<TextChannel> textChannel = target.findTextChannel();
                if (!textChannel.isPresent()) {
                    event.send("`" + target.getArgument() + "` is not a valid text channel.");
                    return;
                }
                final long targetId = textChannel.get().getIdLong();
                if (createOption(event, options, reaction, targetId)) return;
            } else {
                final Optional<Role> role = target.findRole();
                if (!role.isPresent()) {
                    event.send("`" + target.getArgument() + "` is not a valid role.");
                    return;
                }
                final long targetId = role.get().getIdLong();
                if (createOption(event, options, reaction, targetId)) return;
            }
        }
        channel.getMessageById(messageArg.parseLong()).queue(message -> {

        }, throwable -> {
            event.send("Message does not exist.");
        });

    }

    private boolean createOption(CommandEvent event, List<ControlPanelOption> options, CommandArgument reaction,
                                 long targetId) {
        if (reaction.isEmoji()) {
            options.add(new ControlPanelOptionEmoji(0, reaction.getEmoji().getUtf8(), targetId));
        } else if (reaction.isEmote()) {
            final Emote emote = reaction.getEmote();
            if (emote.isFake()) {
                event.reply("`")
                        .append(emote.getAsMention())
                        .append("` is not an emote that I have access to.")
                        .send();
                return true;
            } else {
                options.add(new ControlPanelOptionEmote(0, reaction.getEmote().getIdLong(), targetId));
            }
        }
        return false;
    }

    @Command
    public String help(CommandEvent event) {
        return
                "Please provide the channelId and messageId and " +
                        "then each emote/emoji with its intended channel or role.\n" +
                        "You cannot mix and match roles and channels in the same command\n```\n" +
                        event.getPrefix() + "createCP <channelId> <messageId> <reaction> <targetId> <reaction> " +
                        "<target> ...```";

    }

}
