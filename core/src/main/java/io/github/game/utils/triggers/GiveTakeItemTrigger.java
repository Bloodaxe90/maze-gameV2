package io.github.game.utils.triggers;

import com.badlogic.gdx.Gdx;
import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

/**
 * A more advanced trigger that can either give an item to the player,
 * or take an item from them
 */
public class GiveTakeItemTrigger implements Trigger {

    private final String id;
    private final String item; // The item to be given or taken
    private final boolean give; // True if this trigger gives the item, false if it takes it
    private final String dialogueDefault; // Text to show if conditions aren't met
    private final String toastText; // Toast message to show on first interaction
    private final String itemMoveDialogue; // Text to show when the item is moved
    private final String interactionSprite; // Sprite to change to on first interaction
    private final boolean destroy; // Should the object disappear after being triggered
    private final boolean uncollidable; // Should the object become non-collidable after interaction
    private final boolean event; // Should this increment the event counter
    private final int score; // The amount to increment the score by
    private final TriggerType type; // If the trigger should be triggered on interaction (pressing E) or on touch

    private boolean firstInteraction = true;

    /**
     * Constructor that parses trigger data from a string array
     * @param args The parameters for the trigger, loaded from the Tiled map
     */
    public GiveTakeItemTrigger(String[] args) {
        this.id = args[0];
        this.item = args[1].toLowerCase();
        this.give = Boolean.parseBoolean(args[2].toLowerCase());
        // The dialogue text is loaded from a file using an index from the args (-1 for no text)
        this.dialogueDefault = DialogueLoader.getBlock(id, Integer.parseInt(args[3]));
        this.itemMoveDialogue = DialogueLoader.getBlock(id, Integer.parseInt(args[4]));
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[5]));
        // If the sprite name is "null" the sprite wont change
        this.interactionSprite = args[6].equalsIgnoreCase("null") ? "" : args[6];
        this.destroy = Boolean.parseBoolean(args[7].toLowerCase());
        this.uncollidable = Boolean.parseBoolean(args[8].toLowerCase());
        this.event = Boolean.parseBoolean(args[9].toLowerCase());
        this.score = Integer.parseInt(args[10]);
        this.type = TriggerType.valueOf(args[11].toUpperCase());
    }

    @Override
    public void trigger(Game game) {
        DialogueBox dialogueBox = game.getUiSystem().getDialogueBox();
        Player player = game.getEntitySystem().getPlayer();

        // Check if the trigger conditions are met
        boolean isTouchTrigger = (type == TriggerType.TOUCH);
        boolean isInteractTrigger = (type == TriggerType.INTERACT && player.isInteract());
        player.stopMoving();

        if (!isTouchTrigger && !isInteractTrigger) {
            return;
        }

        // This logic is a bit complex, but it handles the give/take conditions
        // It should trigger if: (we are giving and the player doesn't have it) OR (we are taking and the player does have it)
        if ((!player.hasItem(item) && give) || (player.hasItem(item) && !give)) {
            // Give or take the item based on the 'give' flag
            if (give) {
                player.addItem(item);
            } else {
                player.removeItem(item);
            }
            dialogueBox.showDialogue(itemMoveDialogue);
            game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

            // Add score and show toast message on the first interaction
            if (firstInteraction) {
                if (event) game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText);
                game.getUiSystem().getStatusBar().addScore(score);
                firstInteraction = false;
                if (uncollidable) {
                    game.getEntitySystem().getEntities().get(id).setCollidable(false);
                }
            }
        } else {
            // If the conditions aren't met, show a default message
            dialogueBox.showDialogue(dialogueDefault);
        }

        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setActive(false);
        }

        // Reset the player's interact flag
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
