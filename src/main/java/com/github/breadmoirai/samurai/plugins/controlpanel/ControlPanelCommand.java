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
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ControlPanelCommand {

    @Inject
    public ControlPanelPlugin plugin;
    @Inject
    public EventWaiter waiter;

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
                if (!event.getMember().hasPermission(textChannel.get(), Permission.MANAGE_PERMISSIONS)) {
                    event.send("Missing Permission to `Manage Permissions` in channel: " + textChannel.get()
                            .getAsMention());
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
        if (!isTargetChannel) {
            if (event.requirePermission(Permission.MANAGE_ROLES)) {
                return;
            }
        }
        final long messageId = messageArg.parseLong();
        channel.getMessageById(messageId).queue(message -> {
            final ControlPanel controlPanel = plugin.getData()
                    .createControlPanel(channel.getGuild().getIdLong(), channel.getIdLong(), messageId,
                                        isTargetChannel ? 'C' : 'R', options);
            for (final ControlPanelOption option : controlPanel.getOptions()) {
                if (option instanceof ControlPanelOptionEmoji) {
                    message.addReaction(((ControlPanelOptionEmoji) option).getEmoji()).queue();
                } else {
                    final Emote emote = event.getJDA().getEmoteById(((ControlPanelOptionEmote) option).getEmote());
                    message.addReaction(emote).queue();
                }
            }
            waiter.waitForReaction()
                    .on(message)
                    .action(e -> controlPanel
                            .getOptions()
                            .stream()
                            .filter(option -> option.test(e.getReaction()))
                            .findFirst()
                            .ifPresent(option -> {
                                boolean isAdd = e instanceof MessageReactionAddEvent;
                                controlPanel.getType().operate(option.getTarget(), e.getMember(), isAdd);
                            }))
                    .stopIf((e, i) -> false)
                    .build();
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
        } else {
            final String name = reaction.getArgument();
            final List<Emote> emotes = event.getJDA().getEmotes();
            final Stream<Emote> exact = emotes.stream().filter(emote -> emote.getName().equalsIgnoreCase(name));
            final Stream<Emote> close = emotes.stream()
                    .filter(emote -> emote.getName().toLowerCase().contains(name.toLowerCase()));
            final Optional<Emote> first = Stream.concat(exact, close).findFirst();
            if (!first.isPresent()) {
                event.reply("Could not find an emote that matches the name: `").append(name).append("`");
                return true;
            }
            options.add(new ControlPanelOptionEmote(0, first.get().getIdLong(), targetId));
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
