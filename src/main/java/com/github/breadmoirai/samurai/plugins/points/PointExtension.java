package com.github.breadmoirai.samurai.plugins.points;

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import net.dv8tion.jda.core.OnlineStatus;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class PointExtension extends JdbiExtension {

    public PointExtension(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("Points")) {
            execute("CREATE TABLE Points (\n" +
                    "  Id       BIGINT NOT NULL PRIMARY KEY,\n" +
                    "  Value    DOUBLE DEFAULT 0,\n" +
                    "}");
        }
    }

    public double getPoints(long userId) {
        return selectDouble("SELECT Value FROM Points WHERE Id = ?", userId)
                .orElseGet(() -> {
                    execute("INSERT INTO Prefix (Id) VALUES (?)", userId);
                    return 0.0;
                });
    }

    public PointSession getPointSession(long userId, OnlineStatus status) {
        return new PointSession(userId, getPoints(userId), status, this);
    }

    public void setPoints(long userId, double value) {
        execute("UPDATE Points SET Value = ? WHERE Id = ?", value, userId);
    }

}
