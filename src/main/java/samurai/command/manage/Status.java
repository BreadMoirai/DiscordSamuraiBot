package samurai.command.manage;

import net.dv8tion.jda.core.EmbedBuilder;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.model.SGuild;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.OsuAPI;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.OffsetDateTime;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
@Key("status")
public class Status extends Command {

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        Runtime thisInstance = Runtime.getRuntime();
        int mb = 1024 * 1024;
        final SGuild team = context.getSGuild();
        final EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Status: Connected", null)
                .setColor(Color.GREEN)
                .setFooter("Samurai\u2122", Bot.AVATAR)
                .addField("Global", String.format("**%-14s**`%d`%n**%-14s**`%d`", "Guilds:", Bot.getGuildCount(), "Users:", Bot.getPlayerCount()), true)
                .addField("Time Active", ((FixedMessage) new Uptime().execute(null)).getMessage().getContent(), false)
                .addField("Messages", String.format("**%-15s**`%d`%n**%-20s**`%d`%n**%-14s**`%.2f`", "received:", Bot.CALLS.get(), "sent:", Bot.SENT.get(), "cmds/hr:", 360.0 * Bot.CALLS.get() / ((System.currentTimeMillis() - Bot.START_TIME) / 1000.0)), true)
                .addField("Osu!API", String.format("**%-16s**`%d`%n**%-17s**`%d`", "calls made:", OsuAPI.count.get(), "calls/min:", OsuAPI.count.get() / ((System.currentTimeMillis() - Bot.START_TIME) / 6000)), true)
                .addField("Memory", String.format("**used:\t**`%d MB`%n**total:\t**`%d MB`%n**max: \t**`%d MB`", (thisInstance.totalMemory() - thisInstance.freeMemory()) / mb, thisInstance.totalMemory() / mb, thisInstance.maxMemory() / mb), true)
                .setTimestamp(OffsetDateTime.now());
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int waiting = 0;
        int running = 0;
        for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds())) {
            if (threadInfo.getThreadState() == Thread.State.WAITING) waiting++;
            else if (threadInfo.getThreadState() == Thread.State.RUNNABLE) running++;
        }
        embed.addField("Threads", String.format("**%-14s**`%d`%n**%-13s**`%d`%n**%-13s**`%d`%n**%-15s**`%d`%n**%-16s**`%d`", "current:", threadMXBean.getThreadCount(), "running:", running, "waiting:", waiting, "peak:", threadMXBean.getPeakThreadCount(), "total:", threadMXBean.getTotalStartedThreadCount()), true);
        return FixedMessage.build(embed.build());
    }
}
