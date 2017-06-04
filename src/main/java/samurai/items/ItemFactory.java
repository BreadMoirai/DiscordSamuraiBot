package samurai.items;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import samurai.database.Database;
import samurai.database.dao.ItemDao;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemFactory implements RowMapper<Item>{

    @Override
    public Item map(ResultSet rs, StatementContext ctx) throws SQLException {
        final ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.setItemId(rs.getInt("ItemId"));
        itemBuilder.setType(ItemType.valueOf(rs.getString("Type")));
        itemBuilder.setName(rs.getString("Name"));
        itemBuilder.setRarity(ItemRarity.valueOf(rs.getShort("Rarity")));
        itemBuilder.setValue(rs.getDouble("Value"));
        itemBuilder.setDescription(rs.getString("Description"));
        final double property[] = new double[9];
        for (int i = 6; i < 14; i++) {
            property[i-6] = rs.getDouble(i);
        }
        itemBuilder.setProperties(property);
        return itemBuilder.createItem();
    }

    public static Item getItemById(int itemId) {
        return Database.get().<ItemDao, Item>openDao(ItemDao.class, itemDao -> itemDao.selectItem(itemId));
    }
}
