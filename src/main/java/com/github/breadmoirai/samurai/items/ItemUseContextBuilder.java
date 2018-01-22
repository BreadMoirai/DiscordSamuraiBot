package com.github.breadmoirai.samurai.items;

import com.github.breadmoirai.samurai.points.PointSession;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class ItemUseContextBuilder {
    private Member member;
    private PointSession session;
    private Inventory inventory;
    private String key;
    private List<Item> itemList;
    private ItemSlot itemSlot;

    public ItemUseContextBuilder setMember(Member member) {
        this.member = member;
        return this;
    }

    public ItemUseContextBuilder setSession(PointSession session) {
        this.session = session;
        return this;
    }

    public ItemUseContextBuilder setInventory(Inventory inventory) {
        this.inventory = inventory;
        return this;
    }

    public ItemUseContextBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ItemUseContextBuilder setItemList(List<Item> itemList) {
        this.itemList = itemList;
        return this;
    }

    public ItemUseContextBuilder setItemSlot(ItemSlot itemSlot) {
        this.itemSlot = itemSlot;
        return this;
    }

    public ItemUseContext build() {
        return new ItemUseContext(member, session, inventory, key, itemSlot, itemList);
    }
}