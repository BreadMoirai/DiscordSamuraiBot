package samurai.database.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import samurai.items.Item;
import samurai.items.ItemFactory;

public interface ItemDao {

    @SqlQuery("SELECT * FROM ItemCatalog WHERE ItemId = :id")
    @RegisterRowMapper(ItemFactory.class)
    Item selectItem(@Bind("id") int itemId);
}
