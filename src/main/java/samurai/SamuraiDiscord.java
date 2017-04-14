package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.PermissionUtil;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.GenericCommand;
import samurai.command.annotations.Admin;
import samurai.command.debug.Groovy;
import samurai.messages.MessageManager;
import samurai.discord.listeners.*;
import samurai.messages.base.FixedMessage;

import javax.security.auth.login.LoginException;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class SamuraiDiscord {

    private final int shardId;
    private JDA client;
    private MessageManager messageManager;

    SamuraiDiscord(JDABuilder jdaBuilder) {
        try {
            this.client = jdaBuilder
                    .addEventListener(
                            new DiscordCommandListener(this),
                            new DiscordGameUpdateListener(this),
                            new DiscordMessageListener(this),
                            new DiscordReactionListener(this))
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
        if (client.getGuildById(Bot.SOURCE_GUILD) != null)
            client.addEventListener(new DreadmoiraiSamuraiGuildListener());

        shardId = client.getShardInfo().getShardId();
        client.getPresence().setGame(Game.of(String.format("Shard [%d/%d]", shardId + 1, Bot.SHARD_COUNT)));
        messageManager = new MessageManager(client);
        if (shardId == 0) {
            client.addEventListener(new DiscordPrivateMessageListener());
            Groovy.addBinding("mm", messageManager);
        }

    }

    public void onCommand(Command c) {
        completeContext(c.getContext());
        if (c.getContext().isSource() && c.getContext().getAuthorId() == 232703415048732672L) {
            c.call().ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
            if (c instanceof GenericCommand) {
                messageManager.onCommand((GenericCommand) c);
            }
        } else if (c.isEnabled()) {
            if (c.getClass().isAnnotationPresent(Admin.class)) {
                if (!PermissionUtil.canInteract(c.getContext().getAuthor(), client.getGuildById(String.valueOf(c.getContext().getGuildId())).getSelfMember())) {
                    messageManager.submit(FixedMessage.build("You do not have the appropriate permissions to use this command. Try asking someone of higher status to help you."));
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

    public void shutdown() {
        client.removeEventListener(client.getRegisteredListeners().toArray());
        messageManager.shutdown();
        client.shutdown();
    }

    public JDA getClient() {
        return client;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void onChannelDelete(long channelId) {
        messageManager.remove(channelId);
    }

    public void onMessageDelete(long channelId, long messageId) {
        messageManager.remove(channelId, messageId);
    }

}
