package samurai.action.generic;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.Bot;
import samurai.SamuraiController;
import samurai.SamuraiListener;
import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;
import samurai.osu.OsuJsonReader;

import java.awt.*;
import java.lang.management.ManagementFactory;

import static samurai.Bot.AVATAR;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/15/2017
 */
@Key("status")
@Client
@Guild
public class StatusAction extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        Runtime thisInstance = Runtime.getRuntime();
        int mb = 1024 * 1024;
        final EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Status: " + client.getPresence().getStatus().name(), null)
                .setColor(Color.GREEN)
                .setFooter("Samurai\u2122", AVATAR)
                .addField("Local Status", String.format("**%-19s**`%s", "current state:", guild.isActive() ? String.format("Active`\n**%-20s**`%d`\n**%-18s**`%d`", "score count:", guild.getScoreCount(), "active users:", guild.getUserCount()) : "Inactive`"), false)
                .addField("Global", String.format("**%-14s**`%d`%n**%-14s**`%d`", "Guilds:", client.getGuilds().size(), "Users:", client.getUsers().size()), true)
                .addField("Time Active", new UptimeAction().buildMessage().getMessage().getContent(), false)
                .addField("Messages", String.format("**%-15s**`%d`%n**%-20s**`%d`%n**%-14s**`%.2f`", "received:", SamuraiController.callsMade.get(), "sent:", SamuraiListener.messagesSent.get(), "cmds/hr:", 360.0 * SamuraiController.callsMade.get() / ((System.currentTimeMillis() - Bot.initializationTime) / 1000)), true)
                .addField("Osu!API", String.format("**%-16s**`%d`%n**%-17s**`%d`", "calls made:", OsuJsonReader.count.get(), "calls/min:", OsuJsonReader.count.get() / ((System.currentTimeMillis() - Bot.initializationTime) / 6000)), true)
                .addField("Memory", String.format("**used:\t**`%d MB`%n**total:\t**`%d MB`%n**max: \t**`%d MB`", (thisInstance.totalMemory() - thisInstance.freeMemory()) / mb, thisInstance.totalMemory() / mb, thisInstance.maxMemory() / mb), true)
                .addField("Threads", String.format("**%-14s**`%d`%n**%-15s**`%d`%n**%-16s**`%d`", "active:", ManagementFactory.getThreadMXBean().getThreadCount(), "peak:", ManagementFactory.getThreadMXBean().getPeakThreadCount(), "total:", ManagementFactory.getThreadMXBean().getTotalStartedThreadCount()), true);
        return new FixedMessage()
                .setMessage(new MessageBuilder().append("Statusing....").build()).setConsumer(message -> message.editMessage(new MessageBuilder().setEmbed(embed.setTimestamp(message.getCreationTime()).build()).build()).queue());
    }
}
