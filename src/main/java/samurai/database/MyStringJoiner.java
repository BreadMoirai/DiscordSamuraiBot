package samurai.database;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MyStringJoiner implements RowMapper<String> {
    @Override
    public String map(ResultSet rs, StatementContext ctx) throws SQLException {
        return String.format("`%s` -> `%s`", rs.getString(1), rs.getString(2));
    }
}
