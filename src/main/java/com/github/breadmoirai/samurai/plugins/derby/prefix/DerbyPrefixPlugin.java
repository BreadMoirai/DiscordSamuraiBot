package com.github.breadmoirai.samurai.plugins.derby.prefix;

import com.github.breadmoirai.breadbot.framework.builder.BreadBotBuilder;
import com.github.breadmoirai.breadbot.plugins.prefix.PrefixPlugin;
import com.github.breadmoirai.samurai.plugins.derby.DerbyDatabase;
import com.github.breadmoirai.samurai.plugins.derby.MissingDerbyPluginException;
import net.dv8tion.jda.core.entities.Guild;

public class DerbyPrefixPlugin implements PrefixPlugin {

    private final String defaultPrefix;
    private DerbyPrefixExtension extension;

    public DerbyPrefixPlugin(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    @Override
    public void initialize(BreadBotBuilder builder) {
        if (!builder.hasPlugin(DerbyDatabase.class)) {
            throw new MissingDerbyPluginException();
        }
        final DerbyDatabase database = builder.getPlugin(DerbyDatabase.class);
        this.extension = database.getExtension(jdbi -> new DerbyPrefixExtension(jdbi, defaultPrefix));

        builder.addCommand(DerbyPrefixCommand::new);
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getPrefix(Guild guild) {
        return extension.getPrefix(guild.getIdLong());
    }

    public void setPrefix(long guildId, String prefix) {
        extension.setPrefix(guildId, prefix);
    }
}
