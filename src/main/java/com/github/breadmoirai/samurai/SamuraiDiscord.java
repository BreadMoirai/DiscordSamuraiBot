///*    Copyright 2017 Ton Ly
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//*/
//package com.github.breadmoirai.samurai;
//
//import com.github.breadmoirai.samurai.command.Command;
//import com.github.breadmoirai.samurai.command.CommandContext;
//import com.github.breadmoirai.samurai.command.CommandFactory;
//import com.github.breadmoirai.samurai.command.CommandScheduler;
//import com.github.breadmoirai.samurai.command.annotations.Admin;
//import com.github.breadmoirai.samurai.command.annotations.Creator;
//import com.github.breadmoirai.samurai.command.annotations.Source;
//import com.github.breadmoirai.samurai.command.restricted.Groovy;
//import com.github.breadmoirai.samurai.items.ItemFactory;
//import com.github.breadmoirai.samurai.messages.MessageManager;
//import com.github.breadmoirai.samurai.messages.base.Reloadable;
//import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
//import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
//import com.github.breadmoirai.samurai.plugins.derby.points.DerbyPointPlugin;
//import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
//import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
//import net.dv8tion.jda.core.JDA;
//import net.dv8tion.jda.core.Permission;
//import net.dv8tion.jda.core.entities.Guild;
//import net.dv8tion.jda.core.entities.Member;
//import net.dv8tion.jda.core.entities.Message;
//import net.dv8tion.jda.core.entities.TextChannel;
//import net.dv8tion.jda.core.entities.User;
//import net.dv8tion.jda.core.events.Event;
//import net.dv8tion.jda.core.events.ReadyEvent;
//import net.dv8tion.jda.core.events.ShutdownEvent;
//import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
//import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
//import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
//import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
//import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
//import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
//import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
//import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
//import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
//import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
//import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
//import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
//import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
//import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
//import net.dv8tion.jda.core.hooks.EventListener;
//
//import java.io.EOFException;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.rmi.NoSuchObjectException;
//import java.util.List;
//
//public class SamuraiDiscord implements EventListener {
//
//    private int shardId;
//    private MessageManager messageManager;
//    private DerbyPointPlugin pointTracker;
//    private CommandScheduler commandScheduler;
//
//    SamuraiDiscord() {
//    }
//
//    private void onReady(ReadyEvent event) {
//        JDA client = event.getJDA();
//        shardId = 0;
//        //shardId = client.getShardInfo().getShardId();
//        if (shardId == 0) {
//            Bot.setInfo(new BotInfo(client));
//        }
//        try {
//            ItemFactory.load(event);
//        } catch (NoSuchObjectException e) {
//            Bot.shutdown();
//            return;
//        }
//        messageManager = new MessageManager(client);
//        DerbyDatabase.get().load(event);
//        this.pointTracker = new DerbyPointPlugin();
//        pointTracker.load(event);
//        commandScheduler = new CommandScheduler(this);
//        System.out.println("SamuraiDiscord [" + shardId + "] is ready!");
//        Groovy.addBinding("mm", messageManager);
//        int i = 0;
//        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream("Objects.ser"))) {
//            //noinspection InfiniteLoopStatement
//            while (true) {
//                final Object o = oos.readObject();
//                if (o instanceof Reloadable) {
//                    ((Reloadable) o).reload(this);
//                    System.out.println("o = " + o);
//                    i++;
//                }
//            }
//        } catch (EOFException ignored) {
//            System.out.println(i + " objects read");
//        } catch (FileNotFoundException ignored) {
//            System.out.println("no objects read");
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onEvent(Event event) {
//        if (event instanceof GuildMessageReceivedEvent) {
//            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
//            this.pointTracker.onGuildMessageReceived((GuildMessageReceivedEvent) event);
//        } else if (event instanceof GuildMessageUpdateEvent) {
//            this.onGuildMessageUpdate((GuildMessageUpdateEvent) event);
//        } else if (event instanceof MessageDeleteEvent) {
//            this.onMessageDelete((MessageDeleteEvent) event);
//        } else if (event instanceof TextChannelDeleteEvent) {
//            this.onTextChannelDelete((TextChannelDeleteEvent) event);
//        } else if (event instanceof UserGameUpdateEvent) {
//            this.onUserGameUpdate((UserGameUpdateEvent) event);
//        } else if (event instanceof PrivateMessageReceivedEvent) {
//            this.onPrivateMessageReceived((PrivateMessageReceivedEvent) event);
//        } else if (event instanceof MessageReactionAddEvent) {
//            this.onMessageReactionAddEvent((MessageReactionAddEvent) event);
//        } else if (event instanceof GuildVoiceLeaveEvent) {
//            this.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
//            this.pointTracker.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
//        } else if (event instanceof GuildVoiceJoinEvent) {
//            this.onGuildVoiceJoin((GuildVoiceJoinEvent) event);
//            this.pointTracker.onGuildVoiceJoin((GuildVoiceJoinEvent) event);
//        } else if (event instanceof UserOnlineStatusUpdateEvent) {
//            this.pointTracker.onUserOnlineStatusUpdate((UserOnlineStatusUpdateEvent) event);
//        } else if (event instanceof GuildVoiceMuteEvent) {
//            this.pointTracker.onGuildVoiceMute((GuildVoiceMuteEvent) event);
//        } else if (event instanceof GuildMemberJoinEvent) {
//            this.onGuildMemberJoin((GuildMemberJoinEvent) event);
//        } else if (event instanceof GuildLeaveEvent) {
//            this.onGuildLeave((GuildLeaveEvent) event);
//            this.pointTracker.onGuildLeave((GuildLeaveEvent) event);
//        } else if (event instanceof GuildJoinEvent) {
//            this.onGuildJoin((GuildJoinEvent) event);
//        } else if (event instanceof ReadyEvent) {
//            this.onReady((ReadyEvent) event);
//        } else if (event instanceof ShutdownEvent) {
//            this.onShutdown((ShutdownEvent) event);
//        }
//    }
//
//    private void onGuildJoin(GuildJoinEvent event) {
//        DerbyDatabase.get().getPrefix(event.getGuild().getIdLong());
//    }
//
//    public void onCommand(Command c) {
//        completeContext(c.getContext());
//        if (c.getContext().getAuthorId() == Bot.info().OWNER) {
//            messageManager.onCommand(c);
//            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
//        } else if (c.getClass().isAnnotationPresent(Creator.class)) {
//        } else if (c.getClass().isAnnotationPresent(Source.class) && c.getContext().getGuildId() != Bot.info().SOURCE_GUILD) {
//        } else if (c.isEnabled()) {
//            if (c.getClass().isAnnotationPresent(Admin.class)) {
//                if (!c.getContext().getAuthor().canInteract(c.getContext().getSelfMember()) && !c.getContext().getAuthor().hasPermission(Permission.KICK_MEMBERS)) {
//                    final FixedMessage error = FixedMessage.build("You do not have the appropriate permissions to use this command.");
//                    error.setChannelId(c.getContext().getChannelId());
//                    messageManager.submit(error);
//                    return;
//                }
//            }
//            messageManager.onCommand(c);
//            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
//        }
//    }
//
//    private void completeContext(CommandContext context) {
//        context.setPointTracker(pointTracker);
//        context.setShardId(shardId);
//        context.setCommandScheduler(commandScheduler);
//    }
//
//    private void onShutdown(ShutdownEvent event) {
//        commandScheduler.shutdown();
//        final List<Reloadable> reloadables = messageManager.shutdown();
//        int i = 0;
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Objects.ser"))) {
//            for (Reloadable reloadable : reloadables) {
//                oos.writeObject(reloadable);
//                System.out.println("reloadable = " + reloadable);
//                i++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(i + " messages persisted");
//        pointTracker.shutdown();
//        System.out.printf("Shutdown Shard[%d]", shardId);
//    }
//
//    public MessageManager getMessageManager() {
//        return messageManager;
//    }
//
//    public DerbyPointPlugin getPointTracker() {
//        return pointTracker;
//    }
//
//    private void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//        if (checkMessage(event.getAuthor(), event.getChannel(), event.getMessage())) {
//            this.getMessageManager().onGuildMessageReceived(event);
//            final String prefix = DerbyDatabase.get().getPrefix(event.getGuild().getIdLong());
//            final Command c = CommandFactory.build(event, prefix);
//            if (c != null) {
//                this.onCommand(c);
//            }
//        }
//    }
//
//    private void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
//        if (checkMessage(event.getAuthor(), event.getChannel(), event.getMessage())) {
//            this.getMessageManager().onGuildMessageUpdate(event);
//            final String prefix = DerbyDatabase.get().getPrefix(event.getGuild().getIdLong());
//            final Command c = CommandFactory.build(event, prefix);
//
//            if (c != null) {
//                this.onCommand(c);
//            }
//        }
//    }
//
//    private boolean checkMessage(User author, TextChannel channel, Message message) {
//        return !author.isFake() && !author.isBot() && !message.isPinned() && channel.canTalk();
//    }
//
//    private void onUserGameUpdate(UserGameUpdateEvent event) {
////        if (event.getGuild().getMember(event.getUser()).getGame() == null) return;
////        if (event.getGuild().getMember(event.getUser()).getGame().getName().equalsIgnoreCase("osu!")) {
////            final long discordUserId = event.getUser().getIdLong();
////            final long discordGuildId = event.getGuild().getIdLong();
////            final Optional<OsuSession> sessionOptional = OsuTracker.retrieveSession(discordUserId);
////            final SamuraiGuild guild = Database.get().<GuildDao, SamuraiGuild>openDao(GuildDao.class, guildDao -> guildDao.getGuild(discordGuildId));
////            if (guild != null) {
////                //fix me
////                final OptionalLong any = guild.getChannelModes().stream().filter(longGameModeEntry -> (GameMode.STANDARD.bit() & longGameModeEntry.getValue()) == GameMode.STANDARD.bit()).mapToLong(Pair::getKey).findAny();
////                if (any.isPresent()) {
////                    final long discordOutputChannelId = any.getAsLong();
////                    final TextChannel outputChannel = event.getGuild().getTextChannelById(discordOutputChannelId);
////                    if (outputChannel != null) {
////                        if (sessionOptional.isPresent()) {
////                            sessionOptional.get().addChannel(outputChannel);
////                        } else {
////                            guild.getPlayer(discordUserId).ifPresent(player -> OsuTracker.register(player, outputChannel));
////                        }
////                    } //if the channel Exists
////                    else {
////                        Database.get().<ChannelDao>openDao(ChannelDao.class, channelDao -> channelDao.deleteChannelMode(discordOutputChannelId));
////                    }
////                } //if they have a channel filter
////            } //if there is a sGuild
////        }// if game == osu
//    }
//
//    private void onMessageDelete(MessageDeleteEvent event) {
//        this.getMessageManager().remove(event.getChannel().getIdLong(), event.getMessageIdLong());
//    }
//
//    private void onTextChannelDelete(TextChannelDeleteEvent event) {
//        this.getMessageManager().remove(event.getChannel().getIdLong());
//    }
//
//    private void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
//        if (event.getAuthor().isFake() || event.getAuthor().isBot()) return;
//        this.getMessageManager().onPrivateMessageReceived(event);
//    }
//
//    private void onMessageReactionAddEvent(MessageReactionAddEvent event) {
//        if (!event.getUser().isBot()) {
//            this.getMessageManager().onReaction(event);
//        }
//    }
//
//
//    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
//        final List<Member> members = event.getChannelLeft().getMembers();
//        final Guild guild = event.getGuild();
//        if (members.size() == 1 && members.contains(guild.getSelfMember())) {
//            MusicPlugin.scheduleLeave(guild.getIdLong());
//        }
//    }
//
//
//    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
//        final Guild guild = event.getGuild();
//        if (event.getMember().equals(guild.getSelfMember())) {
//            return;
//        }
//        final List<Member> members = event.getChannelJoined().getMembers();
//        if (members.size() == 2 && members.contains(guild.getSelfMember()) && members.contains(event.getMember())) {
//            MusicPlugin.cancelLeave(guild.getIdLong());
//        }
//    }
//
//    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
//        final Guild guild = event.getGuild();
//        if (guild.getIdLong() == Bot.info().SOURCE_GUILD) {
//            guild.getController().addRolesToMember(event.getMember(), guild.getRolesByName("Peasant", false)).queue();
//        }
//    }
//
//    private void onGuildLeave(GuildLeaveEvent event) {
//        MusicPlugin.retrieveManager(event.getGuild().getIdLong()).ifPresent(GuildAudioManager::destroy);
//    }
//}