package io.github.game.ui.elements;

/**
 * A simple data class to represent an item in the games inventory
 */
public class Item {

    // The name of the item which should match its texture name in the atlas
    private final String name;

    /**
     * Constructor for an Item
     * @param name The name of the item
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * @return The name of the item
     */
    public String getName() {
        return name;
    }
}
