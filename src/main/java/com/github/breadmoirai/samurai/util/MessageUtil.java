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
package com.github.breadmoirai.samurai.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class MessageUtil {

    public static void addReaction(Message message, Iterable<String> s) {
        s.forEach(s1 -> message.addReaction(s1).queue());
    }

    public static void addReaction(Message message, Collection<String> s, Consumer<Void> consumer) {
        final Iterator<String> iterator = s.iterator();
        int i = 0;
        while (i++ != s.size()-1) {
            message.addReaction(iterator.next()).queue(null ,null);
        }
        message.addReaction(iterator.next()).queue(consumer, null);
    }

    public static Message of(CharSequence s) {
        return new MessageBuilder().append(s).build();
    }

}
