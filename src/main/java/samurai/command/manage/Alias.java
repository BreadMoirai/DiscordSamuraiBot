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
package samurai.command.manage;

import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.database.Database;
import samurai.database.dao.AliasDao;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Admin
@Key({"alias", "aliasremove"})
public class Alias extends Command {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final long guildId = context.getGuildId();
        String content = context.getContent();
        if (context.getKey().equalsIgnoreCase("aliasremove")) {
            return FixedMessage.build(Database.get().<AliasDao, Boolean>openDao(AliasDao.class, aliasDao -> aliasDao.deleteAlias(guildId, content) >= 1) ? "Alias successfully removed" : "No such alias found");
        }
        final Matcher matcher = WHITESPACE.matcher(content);
        if (matcher.find()) {
            String alias = content.substring(0, matcher.start());
            String command = content.substring(matcher.end());
            if (Database.get().<AliasDao, Boolean>openDao(AliasDao.class, aliasDao -> {
                final String existingAlias = aliasDao.getAlias(guildId, alias);
                if (existingAlias != null) return Boolean.FALSE;
                else aliasDao.insertAlias(guildId, alias, command);
                return Boolean.TRUE;
            })) return FixedMessage.build(String.format("Alias `%s` mapped to `%s`", alias, command));
            else return FixedMessage.build(String.format("Alias `%s` already exists. Use `%saliasremove` to remove it", alias, context.getPrefix()));
        }
        return FixedMessage.build(Database.get().<AliasDao, List<String>>openDao(AliasDao.class, aliasDao -> aliasDao.getAllAliases(guildId)).stream().collect(Collectors.joining("\n")));
    }
}
