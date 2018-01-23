package com.github.breadmoirai.samurai.plugins.derby.prefix;

import org.jdbi.v3.sqlobject.customizer.Bind;

public interface PrefixDao {

    String getPrefix(@Bind("id") long guildId);

}
