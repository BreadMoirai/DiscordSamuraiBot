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
package samurai.messages.impl.poker;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.messages.base.DynamicMessage;

public class TableLog extends DynamicMessage {


    private TextChannel channel;

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Placeholder").build();
    }

    @Override
    protected void onReady(Message message) {
        channel = message.getTextChannel();
    }

    public void destroy() {
        channel.delete().queue();
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
