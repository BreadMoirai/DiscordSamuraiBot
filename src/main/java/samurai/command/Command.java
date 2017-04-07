package samurai.command;

import samurai.messages.base.SamuraiMessage;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Superclass of all actions
 *
 * @author TonTL
 * @version 4.0
 */
public abstract class Command implements Callable<Optional<SamuraiMessage>> {

    private CommandContext context;

    public Optional<SamuraiMessage> call() {
        Optional<SamuraiMessage> messageOptional = Optional.ofNullable(execute(context));
        messageOptional.ifPresent(samuraiMessage -> samuraiMessage.setChannelId(context.getChannelId()));
        return messageOptional;
    }

    protected abstract SamuraiMessage execute(CommandContext context);

    public CommandContext getContext() {
        return context;
    }

    public void setContext(CommandContext context) {
        this.context = context;
    }

    public boolean isEnabled() {
        return Commands.valueOf(this.getClass().getSimpleName()).isEnabled(context.getSGuild().getEnabledCommands());
    }
}
