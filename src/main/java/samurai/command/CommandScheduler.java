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

import net.dv8tion.jda.core.entities.ISnowflake;
import samurai.SamuraiDiscord;

import java.io.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandScheduler implements Serializable {

    private static final transient ScheduledExecutorService COMMAND_EXECUTOR;

    static {
        COMMAND_EXECUTOR = Executors.newScheduledThreadPool(2);
    }

    private final transient SamuraiDiscord samurai;

    private final transient HashMap<Long, List<CommandTask>> tasks;

    public CommandScheduler(SamuraiDiscord samurai) {
        this.samurai = samurai;
        tasks = new HashMap<>();
        samurai.getMessageManager().getClient().getGuilds().stream().map(ISnowflake::getIdLong).forEach(i -> tasks.put(i, Collections.synchronizedList(new ArrayList<>())));
        initializeTasks();
    }

    public void scheduleCommand(String line, PrimitiveContext context, long targetChannel, Instant time) {
        final long guildId = context.guildId;
        context.channelId = targetChannel;
        tasks.putIfAbsent(guildId, Collections.synchronizedList(new ArrayList<>()));
        new CommandTask(line, context, time, this).schedule();
    }

    private void rescheduleCommand(CommandTask task) {
        tasks.putIfAbsent(task.getContext().guildId, Collections.synchronizedList(new ArrayList<>()));
        task.setScheduler(this);
        task.schedule();
    }

    private void initializeTasks() {
        int i = 0;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("tasks.ser"))) {
            //noinspection InfiniteLoopStatement
            while (true) {
                final Object o = is.readObject();
                if (o instanceof CommandTask) {
                    rescheduleCommand((CommandTask) o);
                    i++;
                }
                else System.out.println("Task: " + o.toString());
            }
        } catch (EOFException ignored) {
            System.out.println(i + " tasks read");
        } catch (FileNotFoundException ignored) {
            System.out.println("no tasks read");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        final List<Runnable> runnables = COMMAND_EXECUTOR.shutdownNow();
        System.out.println("runnables = " + runnables);
        int i = 0;
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("tasks.ser"))) {
            for (List<CommandTask> commandTasks : tasks.values()) {
                for (CommandTask commandTask : commandTasks) {
                    os.writeObject(commandTask);
                    i++;
                }
            }
            if (i == 0) {
                os.writeObject("Null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(i + " tasks written");
    }


    ScheduledFuture<?> schedule(CommandTask commandTask, long between, TimeUnit seconds) {
        return COMMAND_EXECUTOR.schedule(commandTask, between, seconds);
    }

    public SamuraiDiscord getSamurai() {
        return samurai;
    }

    void addTask(long guildId, CommandTask commandTask) {
        tasks.get(guildId).add(commandTask);
    }

    void removeTask(long guildId, CommandTask commandTask) {
        tasks.get(guildId).remove(commandTask);
    }

    public List<CommandTask> getTasks(long guildId) {
        return Collections.unmodifiableList(tasks.get(guildId));
    }

    public static ScheduledExecutorService getCommandExecutor() {
        return COMMAND_EXECUTOR;
    }
}
