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
package samurai.messages.impl.test;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.listeners.ReactionListener;

/**
 * @author TonTL
 * @version 4/19/2017
 */
public class UniqueTestMessage extends DynamicMessage implements ReactionListener, UniqueMessage {


    private MessageScope scope;

    public UniqueTestMessage(MessageScope scope) {
        this.scope = scope;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Only one of these can be active per scope: ").append(scope.toString()).build();
    }

    @Override
    protected void onReady(Message message) {
        message.addReaction("\uD83D\uDC19").queue();
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        if (event.getReaction().getEmote().getName().equals("\uD83D\uDC19")) {
            event.getJDA().getTextChannelById(getChannelId()).getMessageById(getMessageId()).queue(message -> {
                message.editMessage("Deactivated").queue();
                message.clearReactions().queue();
            });
            unregister();
        }
    }

    @Override
    public MessageScope scope() {
        return scope;
    }

    @Override
    public boolean shouldPrompt() {
        return true;
    }

    @Override
    public void close(TextChannel channel) {
        channel.editMessageById(getMessageId(), "This has been removed by another instance of this command").queue(message -> message.clearReactions().queue());
    }
}
