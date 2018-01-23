///*    Copyright 2017 Ton Ly
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//*/
//package com.github.breadmoirai.samurai.command.debug;
//
//import com.github.breadmoirai.samurai.Bot;
//import com.github.breadmoirai.samurai.BotInfo;
//import com.github.breadmoirai.samurai.command.Command;
//import com.github.breadmoirai.samurai.command.CommandContext;
//import com.github.breadmoirai.samurai.command.annotations.Key;
//import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
//import com.github.breadmoirai.samurai.messages.impl.FixedMessage;
//import com.github.breadmoirai.samurai.osu.OsuAPI;
//import net.dv8tion.jda.core.EmbedBuilder;
//
//import java.awt.*;
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadInfo;
//import java.lang.management.ThreadMXBean;
//import java.time.OffsetDateTime;
//
//@Key("status")
//public class Status extends Command {
//
//    @Override
//    protected SamuraiMessage execute(CommandContext context) {
//        Runtime thisInstance = Runtime.getRuntime();
//        int mb = 1024 * 1024;
//        final BotInfo info = Bot.info();
//        final EmbedBuilder embed = new EmbedBuilder()
//                .setTitle("Status: Connected", null)
//                .setColor(Color.GREEN)
//                .setFooter("Samurai\u2122", context.getSelfUser().getEffectiveAvatarUrl())
//                .addField("Global", String.format("**%-14s**`%d`%n**%-14s**`%d`", "Guilds:", Bot.getGuildCount(), "Users:", Bot.getPlayerCount()), true)
//                .addField("Time Active", ((FixedMessage) new Uptime().execute(null)).getMessage().getContent(), false)
//                .addField("Messages", String.format("**%-15s**`%d`%n**%-20s**`%d`%n**%-14s**`%.2f`", "received:", info.CALLS.get(), "sent:", info.SENT.get(), "cmds/hr:", 360.0 * info.CALLS.get() / ((System.currentTimeMillis() - info.START_TIME) / 1000.0)), true)
//                .addField("Osu!API", String.format("**%-16s**`%d`%n**%-17s**`%d`", "calls made:", OsuAPI.calls, "calls/min:", OsuAPI.calls / ((System.currentTimeMillis() - info.START_TIME) / 6000)), true)
//                .addField("Memory", String.format("**used:\t**`%d MB`%n**total:\t**`%d MB`%n**max: \t**`%d MB`", (thisInstance.totalMemory() - thisInstance.freeMemory()) / mb, thisInstance.totalMemory() / mb, thisInstance.maxMemory() / mb), true)
//                .setTimestamp(OffsetDateTime.now());
//        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//        int waiting = 0;
//        int running = 0;
//        for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds())) {
//            if (threadInfo.getThreadState() == Thread.State.WAITING) waiting++;
//            else if (threadInfo.getThreadState() == Thread.State.RUNNABLE) running++;
//        }
//        embed.addField("Threads", String.format("**%-14s**`%d`%n**%-13s**`%d`%n**%-13s**`%d`%n**%-15s**`%d`%n**%-16s**`%d`", "current:", threadMXBean.getThreadCount(), "running:", running, "waiting:", waiting, "peak:", threadMXBean.getPeakThreadCount(), "total:", threadMXBean.getTotalStartedThreadCount()), true);
//        return FixedMessage.build(embed.build());
//    }
//}
