/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.lang3.tuple.Pair;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Source;
import samurai.command.basic.GenericCommand;
import samurai.command.restricted.Groovy;
import samurai.database.Database;
import samurai.database.dao.ChannelDao;
import samurai.database.dao.GuildDao;
import samurai.database.objects.SamuraiGuild;
import samurai.messages.MessageManager;
import samurai.messages.impl.FixedMessage;
import samurai.osu.enums.GameMode;
import samurai.osu.tracker.OsuSession;
import samurai.osu.tracker.OsuTracker;
import samurai.points.PointTracker;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class SamuraiDiscord implements EventListener {

    private int shardId;
    private MessageManager messageManager;
    private PointTracker pointTracker;

    SamuraiDiscord() {
    }

    private void onReady(ReadyEvent event) {
        JDA client = event.getJDA();
        shardId = 0;
        //shardId = client.getShardInfo().getShardId();
        if (shardId == 0) {
            Bot.setInfo(new BotInfo(client));
        }
        messageManager = new MessageManager(client);
        Database.get().load(event);
        this.pointTracker = new PointTracker();
        pointTracker.load(event);
        System.out.println("SamuraiDiscord [" + shardId + "] is ready!");
        Groovy.addBinding("mm", messageManager);
        Groovy.addBinding("points", pointTracker);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildMessageReceivedEvent) {
            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
            this.pointTracker.onGuildMessageReceived((GuildMessageReceivedEvent) event);
        } else if (event instanceof GuildMessageUpdateEvent) {
            this.onGuildMessageUpdate((GuildMessageUpdateEvent) event);
        } else if (event instanceof MessageDeleteEvent) {
            this.onMessageDelete((MessageDeleteEvent) event);
        } else if (event instanceof TextChannelDeleteEvent) {
            this.onTextChannelDelete((TextChannelDeleteEvent) event);
        } else if (event instanceof UserGameUpdateEvent) {
            this.onUserGameUpdate((UserGameUpdateEvent) event);
        } else if (event instanceof PrivateMessageReceivedEvent) {
            this.onPrivateMessageReceived((PrivateMessageReceivedEvent) event);
        } else if (event instanceof MessageReactionAddEvent) {
            this.onMessageReactionAddEvent((MessageReactionAddEvent) event);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            this.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
            this.pointTracker.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
        } else if (event instanceof GuildVoiceJoinEvent) {
            this.onGuildVoiceJoin((GuildVoiceJoinEvent) event);
            this.pointTracker.onGuildVoidJoin((GuildVoiceJoinEvent) event);
        } else if (event instanceof UserOnlineStatusUpdateEvent) {
            this.pointTracker.onUserOnlineStatusUpdate((UserOnlineStatusUpdateEvent) event);
        } else if (event instanceof GuildVoiceMuteEvent) {
            this.pointTracker.onGuildVoiceMute((GuildVoiceMuteEvent) event);
        } else if (event instanceof GuildMemberJoinEvent) {
            this.onGuildMemberJoin((GuildMemberJoinEvent) event);
        } else if (event instanceof GuildLeaveEvent) {
            this.onGuildLeave((GuildLeaveEvent) event);
        } else if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent) event);
        } else if (event instanceof ShutdownEvent) {
            this.onShutdown((ShutdownEvent) event);
        }
    }

    private void onCommand(Command c) {
        completeContext(c.getContext());
        if (c.getContext().getAuthorId() == Bot.info().OWNER) {
            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
            if (c instanceof GenericCommand) {
                messageManager.onCommand((GenericCommand) c);
            }
        } else if (c.getClass().isAnnotationPresent(Creator.class) || (c.getClass().isAnnotationPresent(Source.class) && !c.getContext().isSource())) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (c.isEnabled()) {
            if (c.getClass().isAnnotationPresent(Admin.class)) {
                if (!c.getContext().getAuthor().canInteract(c.getContext().getSelfMember())) {
                    final FixedMessage error = FixedMessage.build("You do not have the appropriate permissions to use this command.");
                    error.setChannelId(c.getContext().getChannelId());
                    messageManager.submit(error);
                    return;
                }
            }
            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
            if (c instanceof GenericCommand) {
                messageManager.onCommand((GenericCommand) c);
            }
        }
    }

    private void completeContext(CommandContext context) {
        context.setPointTracker(pointTracker);
        context.setShardId(shardId);
    }

    private void onShutdown(ShutdownEvent event) {
        messageManager.shutdown();
        pointTracker.shutdown();
        System.out.printf("Shutdown Shard[%d]", shardId);
    }

    MessageManager getMessageManager() {
        return messageManager;
    }

    private void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (checkMessage(event.getAuthor(), event.getChannel(), event.getMessage())) {
            this.getMessageManager().onGuildMessageReceived(event);
            final String prefix = Database.get().getPrefix(event.getGuild().getIdLong());
            final Command c = CommandFactory.build(event, prefix);

            if (c != null) {
                this.onCommand(c);
            }
        }
    }

    private void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (checkMessage(event.getAuthor(), event.getChannel(), event.getMessage())) {
            this.getMessageManager().onGuildMessageUpdate(event);
            final String prefix = Database.get().getPrefix(event.getGuild().getIdLong());
            final Command c = CommandFactory.build(event, prefix);

            if (c != null) {
                this.onCommand(c);
            }
        }
    }

    private boolean checkMessage(User author, TextChannel channel, Message message) {
        return !author.isFake() && !author.isBot() && !message.isPinned() && channel.canTalk();
    }

    private void onUserGameUpdate(UserGameUpdateEvent event) {
//        if (event.getGuild().getMember(event.getUser()).getGame() == null) return;
//        if (event.getGuild().getMember(event.getUser()).getGame().getName().equalsIgnoreCase("osu!")) {
//            final long discordUserId = event.getUser().getIdLong();
//            final long discordGuildId = event.getGuild().getIdLong();
//            final Optional<OsuSession> sessionOptional = OsuTracker.retrieveSession(discordUserId);
//            final SamuraiGuild guild = Database.get().<GuildDao, SamuraiGuild>openDao(GuildDao.class, guildDao -> guildDao.getGuild(discordGuildId));
//            if (guild != null) {
//                //fix me
//                final OptionalLong any = guild.getChannelModes().stream().filter(longGameModeEntry -> (GameMode.STANDARD.bit() & longGameModeEntry.getValue()) == GameMode.STANDARD.bit()).mapToLong(Pair::getKey).findAny();
//                if (any.isPresent()) {
//                    final long discordOutputChannelId = any.getAsLong();
//                    final TextChannel outputChannel = event.getGuild().getTextChannelById(discordOutputChannelId);
//                    if (outputChannel != null) {
//                        if (sessionOptional.isPresent()) {
//                            sessionOptional.get().addChannel(outputChannel);
//                        } else {
//                            guild.getPlayer(discordUserId).ifPresent(player -> OsuTracker.register(player, outputChannel));
//                        }
//                    } //if the channel Exists
//                    else {
//                        Database.get().<ChannelDao>openDao(ChannelDao.class, channelDao -> channelDao.deleteChannelMode(discordOutputChannelId));
//                    }
//                } //if they have a channel filter
//            } //if there is a sGuild
//        }// if game == osu
    }

    private void onMessageDelete(MessageDeleteEvent event) {
        this.getMessageManager().remove(event.getChannel().getIdLong(), event.getMessageIdLong());
    }

    private void onTextChannelDelete(TextChannelDeleteEvent event) {
        this.getMessageManager().remove(event.getChannel().getIdLong());
    }

    private void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isFake() || event.getAuthor().isBot()) return;
        this.getMessageManager().onPrivateMessageReceived(event);
    }

    private void onMessageReactionAddEvent(MessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            this.getMessageManager().onReaction(event);
        }
    }


    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        final List<Member> members = event.getChannelLeft().getMembers();
        final Guild guild = event.getGuild();
        if (members.size() == 1 && members.contains(guild.getSelfMember())) {
            SamuraiAudioManager.scheduleLeave(guild.getIdLong());
        }
    }


    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        final Guild guild = event.getGuild();
        if (event.getMember().equals(guild.getSelfMember())) {
            return;
        }
        final List<Member> members = event.getChannelJoined().getMembers();
        if (members.size() == 2 && members.contains(guild.getSelfMember()) && members.contains(event.getMember())) {
            SamuraiAudioManager.cancelLeave(guild.getIdLong());
        }
    }

    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final Guild guild = event.getGuild();
        if (guild.getIdLong() == Bot.info().SOURCE_GUILD) {
            guild.getController().addRolesToMember(event.getMember(), guild.getRolesByName("Peasant", false)).queue();
        }
    }

    private void onGuildLeave(GuildLeaveEvent event) {
        SamuraiAudioManager.retrieveManager(event.getGuild().getIdLong()).ifPresent(GuildAudioManager::destroy);
    }
}