/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Key("tasks")
public class Tasks extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final List<CommandTask> tasks = context.getCommandScheduler().getTasks(context.getGuildId());
        if (context.getContent().toLowerCase().startsWith("cancel")) {
            final List<CommandTask> cTasks = context.getIntArgs().map(i -> i - 1).filter(i -> i > -1 && i < tasks.size()).mapToObj(tasks::get).collect(Collectors.toList());
            int count = cTasks.size();
            cTasks.forEach(CommandTask::cancel);
            return FixedMessage.build(count + " tasks cancelled");
        }
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        StringJoiner joiner = new StringJoiner("\n");
        int bound = tasks.size();
        int i = 1;
        for (CommandTask task : tasks) {
            String format = String.format("`%d.` %s", i++, task.toDisplay(context.getGuild()));
            joiner.add(format);
        }
        final String collect = joiner.toString();
        return FixedMessage.build(embedBuilder.setDescription(collect).setTimestamp(Instant.now()).build());
    }
}