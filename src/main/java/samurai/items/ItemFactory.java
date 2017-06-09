package samurai.items;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import samurai.database.Database;
import samurai.database.dao.ItemDao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemFactory implements RowMapper<Item>{

    @Override
    public Item map(ResultSet rs, StatementContext ctx) throws SQLException {
        final ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.setItemId(rs.getInt("ItemId"));
        itemBuilder.setType(ItemType.valueOf(rs.getString("Type").toUpperCase()));
        itemBuilder.setName(rs.getString("Name"));
        itemBuilder.setRarity(ItemRarity.valueOf(rs.getShort("Rarity")));
        itemBuilder.setValue(rs.getDouble("Value"));
        itemBuilder.setDescription(rs.getString("Description"));
        itemBuilder.setStackLimit(/*rs.getShort("StackLimit")*/1);
        final double property[] = new double[6];
        final long property2[] = new long[2];
        property[0] = rs.getDouble("PropertyA");
        property[1] = rs.getDouble("PropertyB");
        property[2] = rs.getDouble("PropertyC");
        property[3] = rs.getDouble("PropertyD");
        property[4] = rs.getDouble("PropertyE");
        property[5] = rs.getDouble("PropertyF");
        property2[0] = rs.getLong("PropertyG");
        property2[1] = rs.getLong("PropertyH");
        itemBuilder.setProperties(property);
        itemBuilder.setProperties(property2);
        return itemBuilder.createItem();
    }

    public static Item getItemById(int itemId) {
        return Database.get().<ItemDao, Item>openDao(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
    }
}
