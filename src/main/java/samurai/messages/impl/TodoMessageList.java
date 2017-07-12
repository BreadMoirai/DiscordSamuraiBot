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
