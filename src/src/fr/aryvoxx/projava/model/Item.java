package fr.aryvoxx.projava.model;

public class Item {
    private String name;
    private String description;
    private ItemType type;
    private int value;

    public enum ItemType {
        POTION_VIE("Potion de vie", "Restaure des points de vie"),
        POTION_FORCE("Potion de force", "Augmente temporairement la force"),
        ARME("Arme", "Augmente les dégâts"),
        ARMURE("Armure", "Réduit les dégâts reçus");

        private final String displayName;
        private final String description;

        ItemType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    public Item(String name, String description, ItemType type, int value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public int getValue() { return value; }

    public String getFullDescription() {
        return name + " - " + description + " (" + type.getDisplayName() + ")";
    }
} 