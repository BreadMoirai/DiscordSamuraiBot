/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import samurai.database.dao.ItemDao;
import samurai.items.Inventory;
import samurai.items.Item;
import samurai.items.ItemFactory;
import samurai.items.ItemSlot;

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
