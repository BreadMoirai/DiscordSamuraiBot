package samurai.command;

import samurai.messages.base.SamuraiMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandScheduler {

    private static final ScheduledExecutorService COMMAND_EXECUTOR;

    static {
        COMMAND_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    }


    public void scheduleCommand(Command c, Duration time) {
        scheduleCommand(c, time.getSeconds());
    }

    public void scheduleCommand(Command c, Instant time) {
        scheduleCommand(c, Duration.between(Instant.now(), time));
    }

    public void scheduleCommand(Command c, long secondsFromEpoch) {
        final ScheduledFuture<Optional<SamuraiMessage>> future = COMMAND_EXECUTOR.schedule(c, secondsFromEpoch, TimeUnit.SECONDS);
    }
}
