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
package samurai7.core.response;

import gnu.trove.map.TLongObjectMap;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.react.GenericPrivateMessageReactionEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class DynamicResponse extends Response implements Serializable {


    private transient ResponseHandler responseHandler;

    @Override
    public final void register(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        final TLongObjectMap<List<DynamicResponse>> rmap = responseHandler.getRmap();
        rmap.putIfAbsent(getChannelId(), new ArrayList<>());
        rmap.get(getChannelId()).add(this);
    }

    @Override
    public final void unregister() {
        responseHandler.getRmap().get(getChannelId()).remove(this);
    }

    protected final ResponseHandler getHandler() {
        return responseHandler;
    }


    //event methods

    public void onChannelMessageEvent(GenericGuildMessageEvent event, Message message) {

    }

    protected abstract void onSelfReactionEvent(GenericGuildMessageReactionEvent event);

    @SubscribeEvent
    public final void onGenericGuildMessageReactionEvent(GenericGuildMessageReactionEvent event) {
        if (getMessageId() == event.getMessageIdLong()) onSelfReactionEvent(event);
    }


    public void onPrivateMessageEvent(GenericGuildMessageEvent event, Message message) {

    }

    @SubscribeEvent
    public void onGenericPrivateMessageReactionEvent(GenericPrivateMessageReactionEvent event) {

    }


}
