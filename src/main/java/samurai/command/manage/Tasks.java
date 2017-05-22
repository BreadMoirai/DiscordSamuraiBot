package samurai.command.manage;

import net.dv8tion.jda.core.EmbedBuilder;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandTask;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Key("tasks")
public class Tasks extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<CommandTask> tasks = context.getCommandScheduler().getTasks(context.getGuildId());
        if (context.getContent().toLowerCase().startsWith("cancel")) {
            final long count = context.getIntArgs().map(i -> --i).mapToObj(tasks::get).map(CommandTask::cancel).mapToInt(value -> value ? 1 : 0).sum();
            return FixedMessage.build(count + " tasks cancelled");
        }
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final String collect = IntStream.range(0, tasks.size()).mapToObj(i -> "`" + (i + 1) + tasks.get(i).toDisplay(context.getGuild())).collect(Collectors.joining("\n"));
        return FixedMessage.build(embedBuilder.setDescription(collect).setTimestamp(Instant.now()).build());
    }
}