package com.github.breadmoirai.samurai.messages.impl;

import com.github.breadmoirai.samurai.SamuraiDiscord;
import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.messages.base.DynamicMessage;
import com.github.breadmoirai.samurai.messages.base.Reloadable;
import com.github.breadmoirai.samurai.messages.listeners.CommandListener;
import com.sun.xml.internal.ws.util.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

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