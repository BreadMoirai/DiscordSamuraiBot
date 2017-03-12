package samurai.core;

import samurai.core.command.Command;
import samurai.core.command.annotations.Admin;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.entities.base.SamuraiMessage;
import samurai.core.events.listeners.CommandListener;

import java.util.Optional;
import java.util.concurrent.*;


/**
 * Controller for the SamuraiBot.
 *
 * @author TonTL
 * @version 4.2
 */
class Samurai implements CommandListener {

    private final ExecutorService commandPool;
    private final ScheduledExecutorService executorPool;

    private final BlockingQueue<Future<Optional<SamuraiMessage>>> actionQueue;

    private final MessageManager messageManager;
    private final GuildManager guildManager;


    Samurai(MessageManager messageManager) {
        this.messageManager = messageManager;
        guildManager = new GuildManager();

        commandPool = Executors.newFixedThreadPool(1);

        executorPool = Executors.newScheduledThreadPool(3);
        executorPool.scheduleWithFixedDelay(this::pollAction, 1000, 1, TimeUnit.MILLISECONDS);
        executorPool.scheduleAtFixedRate(this::clearInactive, 60, 15, TimeUnit.MINUTES);

        actionQueue = new LinkedBlockingQueue<>();

    }

    private void pollAction() {
        try {
            Future<Optional<SamuraiMessage>> optionalFuture = actionQueue.take();
            Optional<SamuraiMessage> messageOptional = optionalFuture.get();
            messageOptional.ifPresent(messageManager::submit);
        } catch (ExecutionException e) {
            Bot.logError(e);
        } catch (InterruptedException e) {
            Bot.log("Command Thread Shutdown");
        }
    }

    @Override
    public void onCommand(Command command) {
        command.setGuild(guildManager.getGuild(command.getGuildId()));
        Bot.CALLS.incrementAndGet();

        if (!checkAnts(command)) {
            return;
        }
        if (!actionQueue.offer(commandPool.submit(command)))
            Bot.logError(new RejectedExecutionException("Could not add Action to Queue"));
    }

    private boolean checkAnts(Command command) {
        if (command.getClass().isAnnotationPresent(Source.class) && command.getGuildId() != Long.parseLong(Bot.SOURCE_GUILD)) {
            return false;
        }
        if (command.getClass().isAnnotationPresent(Creator.class) && !command.getAuthor().isOwner())
            return false;
        if (command.getClass().isAnnotationPresent(Admin.class) && !command.getAuthor().canInteract(command.getAuthor().getGuild().getMember(Bot.getUser(Long.valueOf(Bot.ID))))) {
            Bot.log(String.format("%s does not have adequate privileges to use `%s`", command.getAuthor().getEffectiveName(), command.getClass().getAnnotation(Key.class).value()[0]));
            return false;
        }
        return true;
    }

    private void clearInactive() {
        messageManager.clearInactive();
        guildManager.clearInactive();
    }

    String getPrefix(long id) {
        return guildManager.getPrefix(id);
    }

    void shutdown() {
        Bot.log("Shutting Down");
        executorPool.shutdownNow();
        commandPool.shutdownNow();
        messageManager.shutdown();
        guildManager.shutdown();
    }

}
