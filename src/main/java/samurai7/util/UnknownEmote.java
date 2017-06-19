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

package samurai7.util;

import net.dv8tion.jda.client.managers.EmoteManager;
import net.dv8tion.jda.client.managers.EmoteManagerUpdatable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

import java.util.List;

public class UnknownEmote implements Emote {

    private final String name;
    private final long id;
    private final JDA jda;

    public UnknownEmote(String name, long id, JDA jda) {
        this.name = name;
        this.id = id;
        this.jda = jda;
    }

    /**
     * Will always return {@code null}
     * @return {@code null}
     */
    @Override
    public Guild getGuild() {
        return null;
    }


    @Override
    public List<Role> getRoles() {
        return null;
    }

    @Override
    public String getName() {
        return name;
}

    @Override
    public boolean isManaged() {
        return false;
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @Override
    public EmoteManager getManager() {
        return null;
    }

    @Override
    public EmoteManagerUpdatable getManagerUpdatable() {
        return null;
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public long getIdLong() {
        return id;
    }

}
