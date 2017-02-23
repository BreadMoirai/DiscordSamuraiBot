package samurai.action.manage;

import net.dv8tion.jda.core.EmbedBuilder;
import samurai.Bot;
import samurai.SamuraiListener;
import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;
import samurai.osu.OsuJsonReader;

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
@Client
@Guild
public class Status extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        Runtime thisInstance = Runtime.getRuntime();
        int mb = 1024 * 1024;
        final EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Status: " + client.getPresence().getStatus().name(), null)
                .setColor(Color.GREEN)
                .setFooter("Samurai\u2122", AVATAR)
                .addField("Local Status", String.format("**%-19s**`%s`%n**%-20s**`%d`%n**%-20s**`%d`", "current state:", guild.isActive() ? "Active" : "Inactive", "score count:", guild.getScoreCount(), "active users:", guild.getUserCount()), true)
                .addField("Global", String.format("**%-14s**`%d`%n**%-14s**`%d`", "Guilds:", client.getGuilds().size(), "Users:", client.getUsers().size()), true)
                .addField("Time Active", new Uptime().buildMessage().getMessage().getContent(), false)
                .addField("Messages", String.format("**%-15s**`%d`%n**%-20s**`%d`%n**%-14s**`%.2f`", "received:", Bot.CALLS.get(), "sent:", SamuraiListener.messagesSent.get(), "cmds/hr:", 360.0 * Bot.SENT.get() / ((System.currentTimeMillis() - Bot.START_TIME) / 1000.0)), true)
                .addField("Osu!API", String.format("**%-16s**`%d`%n**%-17s**`%d`", "calls made:", OsuJsonReader.count.get(), "calls/min:", OsuJsonReader.count.get() / ((System.currentTimeMillis() - Bot.START_TIME) / 6000)), true)
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
