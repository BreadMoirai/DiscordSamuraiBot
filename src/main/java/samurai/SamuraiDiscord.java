package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.utils.PermissionUtil;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Source;
import samurai.command.generic.GenericCommand;
import samurai.command.restricted.Groovy;
import samurai.database.Database;
import samurai.database.Entry;
import samurai.entities.model.SGuild;
import samurai.messages.MessageManager;
import samurai.messages.base.FixedMessage;
import samurai.osu.enums.GameMode;
import samurai.osu.tracker.OsuSession;
import samurai.osu.tracker.OsuTracker;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class SamuraiDiscord implements EventListener {

    private int shardId;
    private MessageManager messageManager;


    SamuraiDiscord() {
    }

    //    SamuraiDiscord(JDABuilder jdaBuilder) {
//        try {
//            this.client = jdaBuilder
//        } catch (LoginException | RateLimitedException e) {
//            e.printStackTrace();
//        }
//
//        shardId = client.getShardInfo().getShardId();
//        client.getPresence().setGame(Game.of(String.format("Shard [%d/%d]", shardId + 1, Bot.SHARD_COUNT)));
//        messageManager = new MessageManager(client);
//        Groovy.addBinding("mm", messageManager);
// }

    private void onReady(ReadyEvent event) {
        JDA client = event.getJDA();
        shardId = 0;
        client.getPresence().setGame(Game.of(String.format("Shard [%d/%d]", shardId + 1, Bot.SHARD_COUNT)));
        messageManager = new MessageManager(client);
        Groovy.addBinding("mm", messageManager);
        System.out.println("SamuraiDiscord [" + shardId + "] is ready!");
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GenericGuildMessageEvent) {
            this.onGenericGuildMessage((GenericGuildMessageEvent) event);
        } else if (event instanceof MessageDeleteEvent) {
            this.onMessageDelete((MessageDeleteEvent) event);
        } else if (event instanceof TextChannelDeleteEvent) {
            this.onTextChannelDelete((TextChannelDeleteEvent) event);
        } else if (event instanceof UserGameUpdateEvent) {
            this.onUserGameUpdate((UserGameUpdateEvent) event);
        } else if (event instanceof GenericPrivateMessageEvent) {
            this.onGenericPrivateMessageEvent((GenericPrivateMessageEvent) event);
        } else if (event instanceof MessageReactionAddEvent) {
            this.onMessageReactionAddEvent((MessageReactionAddEvent) event);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            this.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
        } else if (event instanceof GuildVoiceJoinEvent) {
            this.onGuildVoiceJoin((GuildVoiceJoinEvent) event);
        } else if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent) event);
        } else if (event instanceof ShutdownEvent) {
            this.onShutdown((ShutdownEvent) event);
        }
    }

    private void onCommand(Command c) {
        completeContext(c.getContext());
        if (c.getContext().getAuthorId() == 232703415048732672L) {
            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
            if (c instanceof GenericCommand) {
                messageManager.onCommand((GenericCommand) c);
            }
        } else if (c.getClass().isAnnotationPresent(Creator.class) || (c.getClass().isAnnotationPresent(Source.class) && !c.getContext().isSource())) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (c.isEnabled()) {
            if (c.getClass().isAnnotationPresent(Admin.class)) {
                if (!PermissionUtil.canInteract(c.getContext().getAuthor(), c.getContext().getClient().getGuildById(String.valueOf(c.getContext().getGuildId())).getSelfMember())) {
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
        context.setShardId(shardId);
    }


    private void onShutdown(ShutdownEvent event) {
        messageManager.shutdown();
        System.out.printf("Shutdown Shard[%d]", shardId);
    }

    MessageManager getMessageManager() {
        return messageManager;
    }

    private void onGenericGuildMessage(GenericGuildMessageEvent event) {
        if (!(event instanceof GuildMessageReceivedEvent || event instanceof GuildMessageUpdateEvent)) {
            return;
        }
        if (event.getAuthor().isFake()) return;
        if (event.getAuthor().getIdLong() == Bot.ID) {
            Bot.SENT.incrementAndGet();
            return;
        } else if (event.getAuthor().isBot()) return;
        if (event.getMessage().isPinned()) return;

        this.getMessageManager().onGuildMessageEvent(event);
        final String prefix = Database.getDatabase().getPrefix(event.getGuild().getIdLong());
        final Command c = CommandFactory.build(event, prefix);

        if (c != null) {
            this.onCommand(c);
        }
    }

    private void onUserGameUpdate(UserGameUpdateEvent event) {
        if (event.getGuild().getMember(event.getUser()).getGame() == null) return;
        if (event.getGuild().getMember(event.getUser()).getGame().getName().equalsIgnoreCase("osu!")) {
            final long discordUserId = event.getUser().getIdLong();
            final long discordGuildId = event.getGuild().getIdLong();
            final Optional<OsuSession> sessionOptional = OsuTracker.retrieveSession(discordUserId);
            final Optional<SGuild> guildOptional = Database.getDatabase().getGuild(discordGuildId, discordUserId);
            if (guildOptional.isPresent()) {
                final SGuild sGuild = guildOptional.get();
                final OptionalLong any = sGuild.getChannelFilters().stream().filter(longGameModeEntry -> longGameModeEntry.getValue() == GameMode.STANDARD).mapToLong(Entry::getKey).findAny();
                if (any.isPresent()) {
                    final long discordOutputChannelId = any.getAsLong();
                    final TextChannel outputChannel = event.getGuild().getTextChannelById(String.valueOf(discordOutputChannelId));
                    if (outputChannel != null) {
                        if (sessionOptional.isPresent()) {
                            sessionOptional.get().addChannel(outputChannel);
                        } else {
                            sGuild.getPlayer(discordUserId).ifPresent(player -> OsuTracker.register(player, outputChannel));
                        }
                    } //if the channel Exists
                    else {
                        Database.getDatabase().removeFilter(discordOutputChannelId);
                    }
                } //if they have a channel filter
            } //if there is a sGuild
        }// if game == osu
    }

    private void onMessageDelete(MessageDeleteEvent event) {
        this.getMessageManager().remove(event.getChannel().getIdLong(), event.getMessageIdLong());
    }

    private void onTextChannelDelete(TextChannelDeleteEvent event) {
        this.getMessageManager().remove(event.getChannel().getIdLong());
    }


    private void onGenericPrivateMessageEvent(GenericPrivateMessageEvent event) {
        if (event instanceof PrivateMessageReceivedEvent || event instanceof PrivateMessageUpdateEvent) {
            if (!event.getAuthor().isBot() && !event.getAuthor().isFake())
                Bot.onPrivateMessageEvent(event);
        }
    }


    private void onMessageReactionAddEvent(MessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            this.getMessageManager().onReaction(event);
        }
    }

    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getMembers().size() == 1) {
            SamuraiAudioManager.removeManager(event.getGuild().getIdLong())
                    .ifPresent(GuildAudioManager::destroy);
        }
    }

    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getChannelJoined().getName().equalsIgnoreCase("music") && !SamuraiAudioManager.retrieveManager(event.getGuild().getIdLong()).isPresent())
            SamuraiAudioManager.openConnection(event.getChannelJoined());
    }

}