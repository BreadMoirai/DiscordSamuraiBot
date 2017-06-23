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
package samurai.messages.impl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import samurai.SamuraiDiscord;
import samurai.command.Command;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.listeners.CommandListener;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TodoMessageItem extends DynamicMessage implements CommandListener
        , Reloadable {

    private static final long serialVersionUID = 124L;

    private String name;
    private List<String> items;
    private int itemCount;
    private transient TextChannel textChannel;

    {
        items = new ArrayList<String>(50) {
            @Override
            public boolean add(String s) {
                if (size() > 40) {
                    remove(40);
                }
                return super.add(s);
            }
        };
    }

    public TodoMessageItem() {}

    public TodoMessageItem(String s) {
        name = StringUtils.capitalize(s);
        itemCount = 0;
    }

    @Override
    protected Message initialize() {
        return buildMessage();
    }

    private Message buildMessage() {
        final MessageBuilder mb = new MessageBuilder();
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(name, null);
        eb.setFooter("Last Updated", null);
        eb.setTimestamp(Instant.now());
        eb.setColor(Color.WHITE);
        int i = itemCount - items.size();
        StringJoiner joiner = new StringJoiner("\n");
        for (String s : items) {
            i++;
            if (s.startsWith("~~")) joiner.add(String.format("%d) %s", i, s));
            else joiner.add(String.format("**%d)** %s", i, s));
        }
        eb.setDescription(joiner.toString());
        return mb.setEmbed(eb.build()).build();
    }


    @Override
    protected void onReady(Message message) {
        textChannel = message.getTextChannel();
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void onCommand(Command command) {
        final String key = command.getContext().getKey();
        if (key.equals(name)) {
            itemCount++;
            items.add(command.getContext().getContent());
            final long messageId = command.getContext().getMessageId();
            if (messageId != 0) {
                textChannel.deleteMessageById(messageId).queue();
            }
            textChannel.editMessageById(getMessageId(), buildMessage()).queue();
        } else if (key.equalsIgnoreCase(name.charAt(0) + "done")) {
            command.getContext().getChannel().deleteMessageById(command.getContext().getMessageId()).queue();
            command.getContext().getIntArgs().filter(value -> value > 0 && value <= items.size()).map(operand -> operand - 1).forEach(value -> items.set(value, "~~" + items.get(value) + "~~"));
            command.getContext().getChannel().editMessageById(getMessageId(), buildMessage()).queue();
        }
    }

    @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        replace(samuraiDiscord.getMessageManager(), getMessageId());
    }
}