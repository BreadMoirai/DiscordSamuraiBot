package com.github.breadmoirai.samurai.plugins.derby.prefix;

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class DerbyPrefixExtension extends JdbiExtension {

    private final String defaultPrefix;

    public DerbyPrefixExtension(Jdbi jdbi, String defaultPrefix) {
        super(jdbi);
        this.defaultPrefix = defaultPrefix;
        if (tableAbsent("Prefix")) {
            execute("CREATE TABLE Prefix (\n" +
                    "  Id       BIGINT      NOT NULL PRIMARY KEY,\n" +
                    "  Value    VARCHAR(16) NOT NULL\n" +
                    ")");
        }
    }

    public String getPrefix(long guildId) {
        final Optional<String> prefix = selectFirst(String.class, "SELECT Value FROM Prefix WHERE Id = ?", guildId);
        if (prefix.isPresent()) {
            return prefix.get();
        } else {
            execute("INSERT INTO Prefix VALUES (?, ?)", guildId, defaultPrefix);
            return defaultPrefix;
        }
    }

    public void setPrefix(long guildId, String prefix) {
        execute("UPDATE Prefix SET Value = ? WHERE Id = ?", prefix, guildId);
    }
}
