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

import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.impl.util.Prompt;
import samurai.messages.listeners.MyEventListener;
import samurai.messages.listeners.SamuraiMessageEventListener;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("ALL")
public class MessageHandler implements MyEventListener{

    private JDA jda;
    private final TLongObjectMap<List<SamuraiMessageEventListener>> messages;

    {
        messages = TCollections.synchronizedMap(new TLongObjectHashMap<>(50));
    }


    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        jda = event.getJDA();
    }

    public JDA getJDA() {
        return jda;
    }

    public void send(SamuraiMessage m) {
        if (!verifyUnique(m));
    }

    void addListener(Object o) {
        if (o instanceof SamuraiMessageEventListener) {
            final SamuraiMessageEventListener observer = (SamuraiMessageEventListener) o;

        }
    }

    void removeListener(Object o) {
        if (o instanceof SamuraiMessageEventListener) {
            final SamuraiMessageEventListener observer = (SamuraiMessageEventListener) o;

        }
    }

    private boolean verifyUnique(SamuraiMessage samuraiMessage) {
        if (samuraiMessage instanceof UniqueMessage) {
            final Class<? extends SamuraiMessage> aClass = samuraiMessage.getClass();
            final UniqueMessage uniqueMessage = (UniqueMessage) samuraiMessage;
            final TextChannel textChannel = getJDA().getTextChannelById(samuraiMessage.getChannelId());
            final long authorId = samuraiMessage.getAuthorId();

            Optional<SamuraiMessageEventListener> optionalPrevious = Optional.empty();
            switch (uniqueMessage.scope()) {
                case Author: {
                    final List<SamuraiMessageEventListener> listeners = messages.get(samuraiMessage.getChannelId());
                    if (listeners != null)
                        optionalPrevious = listeners.stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass && dynamicMessage.getAuthorId() == authorId).findAny();
                    break;
                }
                case Channel: {
                    final List<SamuraiMessageEventListener> listeners = messages.get(samuraiMessage.getChannelId());
                    if (listeners != null)
                        optionalPrevious = listeners.stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
                    break;
                }
                case Guild:
                    optionalPrevious = textChannel.getGuild().getTextChannels().stream().mapToLong(ISnowflake::getIdLong).mapToObj(messages::get).filter(Objects::nonNull).flatMap(List::stream).filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
                    break;
                default:
                    throw new UnsupportedOperationException("Scope not found");
            }

            if (optionalPrevious.isPresent()) {
                final SamuraiMessageEventListener previousMessage = optionalPrevious.get();
                final UniqueMessage previousUnique = (UniqueMessage) previousMessage;
                if (uniqueMessage.shouldPrompt()) {
                    SamuraiMessage prompt = new Prompt(uniqueMessage.prompt(),
                            yesPrompt -> {
                                previousUnique.close(getJDA().getTextChannelById(previousMessage.getChannelId()));
                                yesPrompt.getChannel().clearReactionsById(yesPrompt.getMessageId()).queue();
//                                samuraiMessage.replace(this, yesPrompt.getMessageId());
                            },
                            noPrompt -> noPrompt.getChannel().deleteMessageById(noPrompt.getMessageId()).queue());
                    prompt.setAuthorId(samuraiMessage.getAuthorId());
                    prompt.setChannelId(samuraiMessage.getChannelId());
//                    prompt.send(this);
                    return true;
                } else {
//                    unregister(previousMessage);
                    previousUnique.close(textChannel);
                }
            }
        }
        return false;
    }

}
