package samurai.items;

import net.dv8tion.jda.core.entities.Member;
import samurai.points.PointSession;

import java.util.List;

public class ItemUseContext {
    private Member member;
    private PointSession session;
    private Inventory inventory;
    private String key;
    private List<BaseItem> itemList;

    public ItemUseContext(Member member, PointSession session, Inventory inventory, String key, List<BaseItem> itemList) {
        this.member = member;
        this.session = session;
        this.inventory = inventory;
        this.key = key;
        this.itemList = itemList;
    }

    public Member getMember() {
        return member;
    }

    public PointSession getSession() {
        return session;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getKey() {
        return key;
    }

    public List<BaseItem> getItemList() {
        return itemList;
    }
}
