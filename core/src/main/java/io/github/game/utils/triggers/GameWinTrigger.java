package io.github.game.utils.triggers;

import com.badlogic.gdx.graphics.Color;
import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

/**
 * A special trigger that ends the game in a "win" state if the player has a specific item
 */
public class GameWinTrigger implements Trigger {

    private final String id;
    private final String item; // The required item to win
    private final String dialogueDefault; // Text to show if the player doesn't have the item
    private final String toastText; // Toast message to show on first interaction
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
    public GameWinTrigger(String[] args) {
        this.id = args[0];
        this.item = args[1].toLowerCase();
        // The dialogue text is loaded from a file using an index from the args (-1 for no text)
        this.dialogueDefault = DialogueLoader.getBlock(id, Integer.parseInt(args[2]));
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[3]));
        // If the sprite name is "null" the sprite wont change
        this.interactionSprite = args[4].equalsIgnoreCase("null") ? "" : args[4];
        this.destroy = Boolean.parseBoolean(args[5].toLowerCase());
        this.uncollidable = Boolean.parseBoolean(args[6].toLowerCase());
        this.event = Boolean.parseBoolean(args[7].toLowerCase());
        this.score = Integer.parseInt(args[8]);
        this.type = TriggerType.valueOf(args[9].toUpperCase());
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

        if (player.hasItem(item)) {
            // If the player has the item, they win!
            game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

            // Add score and show toast message on the first interaction
            if (firstInteraction) {
                if (event) game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText, Color.BLUE);
                game.getUiSystem().getStatusBar().addScore(score);
                firstInteraction = false;
                if (uncollidable) {
                    game.getEntitySystem().getEntities().get(id).setCollidable(false);
                }
            }
            // Call the method to set up the "Game Over" screen with a win message
            game.getUiSystem().setupGameOverScreen("Win\nYou made it home in time");
        } else {
            // If the player does NOT have the item, just show a dialogue message
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
