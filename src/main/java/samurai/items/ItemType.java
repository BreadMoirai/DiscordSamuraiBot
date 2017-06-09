package samurai.items;

public enum ItemType {
    VANITY, CONSUMABLE, CRATE, SEED, EGG, LAND, CRAFTING;


    @Override
    public String toString() {
        switch (this) {
            case VANITY: return "Vanity";
            case CONSUMABLE: return "Consumable";
            case CRATE: return "Crate";
            case SEED: return "Seed";
            case EGG: return "Egg";
            case LAND: return "Land";
            case CRAFTING: return "Crafting";
            default: return null;
        }
    }
}
