package com.github.breadmoirai.samurai.database;

import com.github.breadmoirai.samurai.items.Inventory;
import com.github.breadmoirai.samurai.items.Item;
import com.github.breadmoirai.samurai.items.ItemFactory;
import com.github.breadmoirai.samurai.items.ItemSlot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ItemTest {

    private static final long GUILD_ID = 1111111;
    private static final long USER_ID = 2222222;

    private static final int ITEM_A = 103, ITEM_B = 101, ITEM_C = 102;

    @Before
    @After
    public void clearInventory() {
        Inventory.ofMember(GUILD_ID, USER_ID).clear();
    }

    @Test
    public void checkInventory() {
        final Inventory inventory = Inventory.ofMember(GUILD_ID, USER_ID);
        System.out.println("inventory = " + inventory);

        final Item itemA = ItemFactory.getItemById(ITEM_A);
        System.out.println("itemA = " + itemA);
        final Item itemB = ItemFactory.getItemById(ITEM_B);
        System.out.println("itemB = " + itemB);
        final Item itemC = ItemFactory.getItemById(ITEM_C);
        System.out.println("itemC = " + itemC);

        inventory.addItem(itemA);
        {
            final List<ItemSlot> itemSlots = Inventory.ofMember(GUILD_ID, USER_ID).getItemSlots();
            System.out.println("itemSlots = " + itemSlots);
        }
        inventory.addItem(itemA);
        {
            final List<ItemSlot> itemSlots = Inventory.ofMember(GUILD_ID, USER_ID).getItemSlots();
            System.out.println("itemSlots = " + itemSlots);
        }
    }


}
