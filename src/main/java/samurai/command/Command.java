package samurai.command;

import samurai.entities.base.SamuraiMessage;

import java.util.Optional;

/**
 * Superclass of all actions
 *
 * @author TonTL
 * @version 4.0
 */
public abstract class Command {

    private CommandContext context;

    public void setContext(CommandContext context) {
        this.context = context;
    }

    public Optional<SamuraiMessage> call() {
        Optional<SamuraiMessage> messageOptional = Optional.ofNullable(execute(context));
        messageOptional.ifPresent(samuraiMessage -> samuraiMessage.setChannelId(context.getChannelId()));
        return messageOptional;
    }

    protected abstract SamuraiMessage execute(CommandContext context);


    public CommandContext getContext() {
        return context;
    }
}
