package samurai.command.debug;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.TodoMessageList;

import java.util.Arrays;

@Creator
@Key("createtodo")
public class CreateTodo extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        context.getChannel().deleteMessageById(context.getMessageId()).queue();
        return new TodoMessageList(Arrays.asList("Bug", "Enhancement", "Feature"));
    }
}
