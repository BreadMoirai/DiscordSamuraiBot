package samurai.items;

import net.dv8tion.jda.core.entities.Member;
import samurai.points.PointSession;

import java.util.List;

public class ItemUseContextBuilder {
    private Member member;
    private PointSession session;
    private Inventory inventory;
    private String key;
    private List<BaseItem> itemList;

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

    public ItemUseContextBuilder setItemList(List<BaseItem> itemList) {
        this.itemList = itemList;
        return this;
    }

    public ItemUseContext createItemUseContext() {
        return new ItemUseContext(member, session, inventory, key, itemList);
    }
}