/*
 *       Copyright 2017 Ton Ly
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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.List;
import java.util.function.Predicate;

public interface ICommandProcessor {

    @SubscribeEvent
    default void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final Message message = event.getMessage();
        for (Predicate<Message> predicate : getPreProcessPredicates()) {
            if (!predicate.test(message)) return;
        }
        fireEvent(processMessage(message));
    }

    List<Predicate<Message>> getPreProcessPredicates();

    void addPreProcessPredicate(Predicate<Message> predicate);

    ICommandEvent processMessage(Message message);

    void fireEvent(ICommandEvent event);
}
