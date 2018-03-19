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
package com.github.breadmoirai.samurai.messages.impl.util;

import com.github.breadmoirai.samurai.messages.base.DynamicMessage;
import com.github.breadmoirai.samurai.messages.listeners.ReactionListener;
import com.github.breadmoirai.samurai.util.MessageUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
public class Book extends DynamicMessage implements ReactionListener {

    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("?", "\uD83D\uDD12", "?"));
    private static final int PAGE_SIZE = 10;

    private final ArrayList<String> book;
    private int page;

    public Book(int page, ArrayList<String> book) {
        this.page = page;
        this.book = book;
    }

    public Book(ArrayList<String> book) {
        this(0, book);
    }

    @Override
    protected Message initialize() {
        if (book.size() == 1) {
            return new MessageBuilder().append(book.get(0)).build();
        } else {
            return new MessageBuilder().append("```md < Binding Pages > ```").build();
        }
    }

    @Override
    protected void onReady(Message message) {
        if (book.size() == 1) {
            MessageUtil.addReaction(message, REACTIONS, aVoid -> message.editMessage(book.get(page)).queue());
        } else {
            unregister();
        }
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final int i = REACTIONS.indexOf(event.getReaction().getEmote().getName());
        final Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        if (i == -1) return;
        if (i == 1) {
            message.clearReactions().queue();
            unregister();
        } else {
            if (i == 0) if (page != 0) page--;
            else return;
            else if (i == 2) if (page != book.size() - 1) page++;
            else return;
            else return;
            message.editMessage(book.get(page)).queue();
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}

