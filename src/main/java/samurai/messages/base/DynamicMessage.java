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
package samurai.messages.base;

import net.dv8tion.jda.core.entities.Message;
import samurai.messages.MessageManager;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * @author TonTL
 * @version 4.x - 3/10/2017
 */
public abstract class DynamicMessage extends SamuraiMessage {


    private static final int TIMEOUT = 24;

    private transient Instant lastActive;
    private transient MessageManager manager;

    {
        lastActive = Instant.now();
    }

    public DynamicMessage() {}

    protected static Consumer<Message> newMenu(List<String> emoji) {
        return message -> emoji.forEach(reaction -> message.addReaction(reaction).complete());
    }

    public boolean isExpired() {
        return HOURS.between(lastActive, Instant.now()) > TIMEOUT;
    }

    /**
     * Unregisters this object with the messageManager.
     * This object will stop receiving events and should fall to garbage collection
     */
    protected void unregister() {
        manager.unregister(this);
    }

    /**
     * Registers this object with the messageManager.
     * Doing so allows the object to receive events for listener interface <? extends SamuraiListener>
     */
    protected void register() {
        manager.register(this);
    }

    @Override
    public void send(MessageManager messageManager) {
        setActive();
        this.manager = messageManager;
        super.send(messageManager);
        register();
    }

    @Override
    public void replace(MessageManager messageManager, long messageId) {
        setActive();
        this.manager = messageManager;
        super.replace(messageManager, messageId);
        register();
    }

    @Override
    public void replace(MessageManager messageManager, Message message) {
        setActive();
        this.manager = messageManager;
        super.replace(messageManager, message);
        register();
    }

    /**
     * use this to refresh the timeout of your message. By default, dynamic message are automatically unregistered at 120 minutes after initialization.
     */
    public void setActive() {
        lastActive = Instant.now();
    }

    public MessageManager getManager() {
        return manager;
    }
}
