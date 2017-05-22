/*    Copyright 2017 Ton Ly

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.command;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandTask implements Runnable, Serializable {
    private static final long serialVersionUID = 360L;

    private OffsetDateTime end;
    private PrimitiveContext context;
    private transient CommandScheduler scheduler;
    private transient ScheduledFuture<?> future;

    public CommandTask() {
    }


    CommandTask(String line, PrimitiveContext baseContext, OffsetDateTime time, CommandScheduler scheduler) {
        context = CommandFactory.buildContextFromTask(line, baseContext);
        end = time;
        this.scheduler = scheduler;
    }

    public void setScheduler(CommandScheduler scheduler) {
        this.scheduler = scheduler;
    }

    void schedule() {
        final long between = ChronoUnit.SECONDS.between(OffsetDateTime.now(), end);
        if (between < 2) {
            executeTask();
        } else {
            scheduler.addTask(context.guildId, this);
            future = scheduler.schedule(this, between, TimeUnit.SECONDS);
        }
    }

    public boolean cancel() {
        scheduler.removeTask(context.guildId, this);
        return future.cancel(false);
    }

    @Override
    public void run() {
        scheduler.tasks.get(context.guildId).remove(this);
        executeTask();
    }

    private void executeTask() {
        final Command command = CommandFactory.newCommand(context.key);
        if (command != null) {
            final CommandContext commandContext = context.buildContext(scheduler.getSamurai().getMessageManager().getClient());
            command.setContext(commandContext);
            scheduler.getSamurai().onCommand(command);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommandTask{");
        sb.append(context.prefix).append(context.key);
        sb.append(' ').append(context.content);
        return sb.toString();
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public PrimitiveContext getContext() {
        return context;
    }

    public String toDisplay(Guild guild) {
        final Member member = guild.getMemberById(context.authorId);
        final Duration until = Duration.between(Instant.now(), end);
        return String.format("[%s]`%s%s %s` T-minus %s", member != null ? member.getEffectiveName(): "Unknown", context.prefix, context.key, context.content, until.toString().substring(2));
    }
}