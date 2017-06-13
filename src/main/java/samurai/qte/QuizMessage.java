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
package samurai.qte;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TLongByteHashMap;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.Bot;
import samurai.command.CommandContext;
import samurai.command.basic.GenericCommand;
import samurai.messages.annotations.GhostMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.listeners.GenericCommandListener;

@GhostMessage
public class QuizMessage extends QuickTimeMessage implements GenericCommandListener {

    private static final long serialVersionUID = 10L;

    private static final long ZERO, SHRUG;
    private static final String ONE = "1\u20E3", TWO = "2\u20E3", THREE = "3\u20E3";

    static {
        final Config config = ConfigFactory.load("source_commands");
        ZERO = config.getLong("prompt.no");
        SHRUG = 316687021513113600L;

    }

    private QuickTimeEventSupplier supplier;
    private TLongByteHashMap users;


    public QuizMessage() {
    }

    public QuizMessage(QuickTimeEventSupplier supplier) {
        this.supplier = supplier;
        users = new TLongByteHashMap(15, Constants.DEFAULT_LOAD_FACTOR, Constants.DEFAULT_LONG_NO_ENTRY_VALUE, (byte) 4);
    }


    @Override
    protected Message initialize() {
        return new MessageBuilder().append("<@&322810785527234561>").setEmbed(supplier.getQuestion()).build();
    }

    @Override
    protected void onReady(Message message) {
        //do nothing
    }

    @Override
    public void onGenericCommand(GenericCommand command) {
        final CommandContext context = command.getContext();
        switch (context.getKey().toLowerCase()) {
            case "answer":
            case "whois":
            case "whatis":
                if (users.get(context.getAuthorId()) > 0 && supplier.checkAnswer(context)) {
                    if (supplier.canProvide()) {
                        context.getChannel().sendMessage(initialize()).queue(this::setMessageId);
                    } else {
                        unregister();
                        final SamuraiMessage reward = supplier.getReward();
                        reward.setChannelId(getChannelId());
                        getManager().submit(reward);
                        fireCompletionEvent(false);
                    }
                } else {
                    switch (users.adjustOrPutValue(context.getAuthorId(), (byte) -1, (byte) 3)) {
                        case 3:
                            context.getChannel().addReactionById(context.getMessageId(), THREE).queue();
                            break;
                        case 2:
                            context.getChannel().addReactionById(context.getMessageId(), TWO).queue();
                            break;
                        case 1:
                            context.getChannel().addReactionById(context.getMessageId(), ONE).queue();
                            break;
                        case 0:
                            context.getChannel().addReactionById(context.getMessageId(), context.getClient().getEmoteById(ZERO)).queue();
                            break;
                        default:
                            context.getChannel().addReactionById(context.getMessageId(), context.getClient().getEmoteById(SHRUG)).queue();
                            break;
                    }
                }
                break;
            case "question":
                context.getChannel().sendMessage(supplier.getQuestion()).queue(this::setMessageId);
                break;
            case "/invalid":
                if (context.getAuthor().canInteract(context.getSelfMember())) {
                    supplier.markInvalid();
                    context.getChannel().deleteMessageById(getMessageId()).queue();
                    if (supplier.canProvide()) {
                        context.getClient().getTextChannelById(getChannelId()).sendMessage(initialize()).queue(this::setMessageId);
                        break;
                    } else {
                        unregister();
                        fireCompletionEvent(true);
                    }
                }
                break;
            case "//invalid":
                if (context.getAuthor().canInteract(context.getSelfMember())) {
                    unregister();
                    context.getChannel().deleteMessageById(getMessageId()).queue();
                    fireCompletionEvent(true);
                }
                break;
            case "/validate":
                if (context.getAuthorId() == Bot.info().OWNER) {
                    context.getChannel().deleteMessageById(context.getMessageId()).queue();
                    context.getAuthor().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(supplier.getAnswer()).queue());
                }
                break;
        }
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
