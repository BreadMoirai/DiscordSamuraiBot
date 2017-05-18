package samurai.messages.impl;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.messages.base.DynamicMessage;

import java.util.ArrayList;
import java.util.List;

public class TodoMessageList extends DynamicMessage {

    private List<TodoMessageItem> items;


    public TodoMessageList(List<String> name) {
        items = new ArrayList<>(5);
        for (String s : name) {
            items.add(new TodoMessageItem(s));
        }
    }

    @Override
    protected Message initialize() {
        return createMessage();
    }

    private Message createMessage() {
        return new MessageBuilder().append("**Big List of Things to be done.**").build();
    }

    @Override
    protected void onReady(Message message) {
        for (TodoMessageItem item : items) {
            item.setGuildId(getGuildId());
            item.setChannelId(getChannelId());
            item.send(getManager());
        }
        unregister();
    }

}
