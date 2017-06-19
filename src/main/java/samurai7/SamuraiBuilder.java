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
package samurai7;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import samurai7.core.IModule;
import samurai7.core.impl.CommandEventProcessor;

import javax.security.auth.login.LoginException;
import java.util.LinkedList;
import java.util.List;

public class SamuraiBuilder {

    private String token;
    private String prefix = "!";
    private long sourceGuild;
    private long ownerId;
    private boolean allowMentionPrefix;
    private String game;
    private List<IModule> modules = new LinkedList<>();

    public SamuraiBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    public SamuraiBuilder setDefaultPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public SamuraiBuilder setSourceGuild(long sourceGuild) {
        this.sourceGuild = sourceGuild;
        return this;
    }

    public SamuraiBuilder setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public SamuraiBuilder setAllowMentionPrefix(boolean allowMentionPrefix) {
        this.allowMentionPrefix = allowMentionPrefix;
        return this;
    }

    public SamuraiBuilder setGame(String game) {
        this.game = game;
        return this;
    }

    public SamuraiBuilder installModule(IModule module) {
        modules.add(module);
        return this;
    }

    public SamuraiBuilder installModules(List<IModule> moduleList) {
        modules.addAll(moduleList);
        return this;
    }

    public void buildAsync() {
        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setAudioEnabled(true)
                    .setEventManager(new AnnotatedEventManager())
                    .setGame(game != null ? Game.of(game): null)
                    .addEventListener(new CommandEventProcessor(modules))
                    .addEventListener(modules.toArray())
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }
}
