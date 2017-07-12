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
package samurai.command;

import net.dv8tion.jda.core.Permission;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Superclass of all actions
 *
 * @author TonTL
 * @version 4.0
 */
public abstract class Command implements Callable<Optional<SamuraiMessage>> {

    private CommandContext context;

    public Optional<SamuraiMessage> call() {
        Optional<SamuraiMessage> messageOptional = Optional.ofNullable(execute(context));
        messageOptional.ifPresent(samuraiMessage -> {
            if (samuraiMessage.getAuthorId() == 0) samuraiMessage.setAuthorId(context.getAuthorId());
            if (samuraiMessage.getChannelId() == 0) samuraiMessage.setChannelId(context.getChannelId());
            if (samuraiMessage.getGuildId() == 0) samuraiMessage.setGuildId(context.getGuildId());
        });
        return messageOptional;
    }

    protected abstract SamuraiMessage execute(CommandContext context);

    public CommandContext getContext() {
        return context;
    }

    public void setContext(CommandContext context) {
        this.context = context;
    }

    public boolean isEnabled() {
        final String name = this.getClass().getPackage().getName();
        return CommandModule.valueOf(name.substring(name.lastIndexOf('.') + 1)).isEnabled(context.getSamuraiGuild().getModules());
    }
}
