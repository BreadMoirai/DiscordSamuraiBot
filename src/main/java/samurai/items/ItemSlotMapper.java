package samurai.items;

import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemSlotMapper implements org.jdbi.v3.core.mapper.RowMapper<ItemSlot> {

    private ItemFactory itemFactory;

    {
        itemFactory = new ItemFactory();
    }

    @Override
    public ItemSlot map(ResultSet rs, StatementContext ctx) throws SQLException {
        final Item item = itemFactory.map(rs, ctx);
        final int slotId = rs.getInt("SlotId");
        final int count = rs.getInt("Count");
        final long userId = rs.getInt("DiscordId");
        final long guildId = rs.getInt("GuildId");
        return new ItemSlotBuilder().setGuildId(guildId).setUserId(userId).setSlotId(slotId).setItem(item).setCount(count).build();
    }
}
