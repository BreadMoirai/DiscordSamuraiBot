package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.GenericCommand;
import samurai.command.restricted.Groovy;
import samurai.core.GuildManager;
import samurai.core.MessageManager;
import samurai.discord.listeners.*;

import javax.security.auth.login.LoginException;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class SamuraiDiscord {

    private final int shardId;
    private JDA client;
    private GuildManager guildManager;
    private MessageManager messageManager;

    SamuraiDiscord(JDABuilder jdaBuilder) {
        try {
            this.client = jdaBuilder
                    .addListener(new DiscordCommandListener(this))
                    .addListener(new DiscordGameUpdateListener(this))
                    .addListener(new DiscordMessageListener(this))
                    .addListener(new DiscordReactionListener(this))
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
        if (client.getGuildById(Bot.SOURCE_GUILD) != null)
            client.addEventListener(new DreadmoiraiSamuraiGuildListener());

        shardId = client.getShardInfo().getShardId();
        client.getPresence().setGame(Game.of("Shard " + client.getShardInfo().getShardString()));
        guildManager = new GuildManager();
        messageManager = new MessageManager(client);
        if (shardId == 0) {
            client.addEventListener(new DiscordPrivateMessageListener());
            Groovy.addBinding("gm", guildManager);
            Groovy.addBinding("mm", messageManager);
        }

    }

    public String getPrefix(long guildId) {
        return guildManager.getPrefix(guildId);
    }


    public void onCommand(Command c) {
        completeContext(c.getContext());
        if (c.isEnabled()) {
            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
            if (c instanceof GenericCommand) {
                messageManager.onCommand((GenericCommand) c);
            }
        }
    }

    private void completeContext(CommandContext context) {
        context.setShardId(shardId);
        context.setGuild(guildManager.getGuild(context.getGuildId()));
    }

    public void shutdown() {
        client.removeEventListener(client.getRegisteredListeners().toArray());
        guildManager.shutdown();
        messageManager.shutdown();
        client.shutdown();
    }

    public JDA getClient() {return client;}

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void onChannelDelete(long channelId) {
        messageManager.remove(channelId);
    }

    public void onMessageDelete(long channelId, long messageId) {
        messageManager.remove(channelId, messageId);
    }


    public GuildManager getGuildManager() {
        return guildManager;
    }
}
