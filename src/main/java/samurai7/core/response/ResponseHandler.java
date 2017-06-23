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

import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.impl.util.Prompt;
import samurai7.core.response.listener.DynamicResponseEventListener;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ResponseHandler {

    private JDA jda;
    private final TLongObjectMap<List<DynamicResponse>> rmap;

    {
        rmap = TCollections.synchronizedMap(new TLongObjectHashMap<>(50));
    }


    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        jda = event.getJDA();
    }

    public void submit(Response r) {
        if (!verifyUnique(r)) send(r);
    }

    private void send(Response r) {
        final TextChannel channel = jda.getTextChannelById(r.getChannelId());
        if (channel != null) {
            channel.sendMessage(r.getMessage()).queue(r::onSend);
            r.register(this);
        }
    }

    private boolean verifyUnique(Response r) {
        if (r instanceof UniqueResponse) {
            final UniqueResponse ur = (UniqueResponse) r;
            final TextChannel channel = jda.getTextChannelById(r.getChannelId());
            final long author = r.getAuthorId();
            Optional<? extends Response> existingResponse;
            final Predicate<DynamicResponse> authorPredicate = r2 -> r2.getAuthorId() == author;
            final Predicate<DynamicResponse> classPredicate = r2 -> r2.getClass() == r.getClass();
            switch (ur.scope()) {
                case AUTHOR_CHANNEL: {
                    final List<DynamicResponse> rlist = rmap.get(r.getChannelId());
                    if (rlist == null) return true;
                    else existingResponse = rlist.stream().filter(authorPredicate).filter(classPredicate).findAny();
                    break;
                }
                case AUTHOR_GUILD: {
                    final Guild guild = jda.getGuildById(r.getGuildId());
                    if (guild == null) return true;
                    else existingResponse = guild.getTextChannels().stream().mapToLong(ISnowflake::getIdLong).mapToObj(rmap::get).filter(Objects::nonNull).flatMap(Collection::stream).filter(authorPredicate).filter(classPredicate).findAny();
                    break;
                }
                case CHANNEL: {
                    final List<DynamicResponse> rlist = rmap.get(r.getChannelId());
                    if (rlist == null) return true;
                    else existingResponse = rlist.stream().filter(classPredicate).findAny();
                    break;
                }
                case GUILD: {
                    final Guild guild = jda.getGuildById(r.getGuildId());
                    if (guild == null) return true;
                    else existingResponse = guild.getTextChannels().stream().mapToLong(ISnowflake::getIdLong).mapToObj(rmap::get).filter(Objects::nonNull).flatMap(Collection::stream).filter(classPredicate).findAny();
                    break;
                }
                default: return true;
            }
            if (!existingResponse.isPresent()) return true;

            final Response r0 = existingResponse.get();
            final UniqueResponse ur0 = (UniqueResponse) r0;
            if (ur.shouldPrompt()) {
                new ResponsePrompt()
            }

            if (existingResponse.isPresent()) {
                final DynamicResponseEventListener previousMessage = optionalPrevious.get();
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

    TLongObjectMap<List<DynamicResponse>> getRmap() {
        return rmap;
    }
}
