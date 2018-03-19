package com.github.breadmoirai.samurai.plugins.derby.prefix;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.plugins.admin.AdminPlugin;

public class DerbyPrefixCommand {

    @MainCommand("prefix")
    public String prefix(
            DerbyPrefixPlugin prefixPlugin,
            AdminPlugin adminPlugin,
            String newPrefix,
            CommandEvent event) {
        if (newPrefix != null) {
            if (adminPlugin == null || adminPlugin.isAdmin(event.getMember())) {
                prefixPlugin.setPrefix(event.getGuildId(), newPrefix);
                return "Prefix Changed: `" + event.getPrefix() + "` -> `" + newPrefix + "`";
            }
        }
        return event.getPrefix();
    }
}
