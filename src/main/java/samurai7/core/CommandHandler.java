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

package samurai7.core;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Source;
import samurai.database.Database;
import samurai7.core.MessageHandler;

import java.util.concurrent.CompletableFuture;

public class CommandHandler {

    private final MessageHandler messageHandler;

    CommandHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        onGuildMessageUpdateOrReceive(event, event.getAuthor(), event.getMessage());
    }

    @SubscribeEvent
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        onGuildMessageUpdateOrReceive(event, event.getAuthor(), event.getMessage());
    }

    private void onGuildMessageUpdateOrReceive(GenericGuildMessageEvent event, User author, Message message) {
//        if (!author.isFake() && !author.isBot() && !message.isPinned() && event.getChannel().canTalk()) {
//            CompletableFuture.supplyAsync(() -> CommandFactory.buildCommand(event, Database.get().getPrefix(event.getGuild().getIdLong()), author, message)).thenAcceptAsync(this::onCommand);
//        }
    }


    private void onCommand(Command c) {
//        completeContext(c.getContext());
//        if (c.getClass().isAnnotationPresent(Creator.class))
//            if (c.getContext().getAuthorId() != Bot.info().OWNER) return;
//        if (c.getClass().isAnnotationPresent(Source.class))
//            if (c.getContext().getGuildId() != Bot.info().SOURCE_GUILD) return;
//        if (c.getClass().isAnnotationPresent(Admin.class))
//            if (!c.getContext().getMember().canInteract(c.getContext().getSelfMember()))
//                if (!c.getContext().getMember().canInteract(c.getContext().getSelfMember()) && !c.getContext().getMember().hasPermission(Permission.KICK_MEMBERS)) {
//                    c.getContext().getChannel().sendMessage("Your access level is not high enough to use this command").queue();
//                    return;
//                }
//
//        messageHandler.onCommand(c);
//        c.call().ifPresent(messageHandler::send);
    }

    private void completeContext(CommandContext context) {

    }
}
