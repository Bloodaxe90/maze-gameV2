package io.github.game.utils.triggers;

import com.badlogic.gdx.graphics.Color;
import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

/**
 * NEW CLASS: (explanation for why in Trigger class)
 *
 * A trigger that starts a dialogue conversation when activated
 */
public class DialogueTrigger implements Trigger {

    private final String id;
    private final String dialogue; // Dialogue to be displayed on interaction
    private String toastText; // Toast message to show on first interaction
    private final String interactionSprite; // Sprite to change to on first interaction
    private final boolean destroy; // Should the object disappear after being triggered
    private final boolean uncollidable; // Should the object become non-collidable after interaction
    private final boolean event; // Should this increment the event counter
    private final int score; // The amount to increment the score by
    private final TriggerType type; // If the trigger should be triggered on interaction (pressing E) or on touch

    // A flag to make sure score/events are only added once
    private boolean firstInteraction = true;

    /**
     * Constructor that parses trigger data from a string array
     * @param args The parameters for the trigger, loaded from the Tiled map
     */
    public DialogueTrigger(String[] args) {
        this.id = args[0];
        // The dialogue text is loaded from a file using an index from the args (-1 for no text)
        this.dialogue = DialogueLoader.getBlock(id, Integer.parseInt(args[1]));
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[2]));

        // If the sprite name is "null" the sprite wont change
        this.interactionSprite = args[3].equalsIgnoreCase("null") ? "" : args[3];
        this.destroy = Boolean.parseBoolean(args[4].toLowerCase());
        this.uncollidable = Boolean.parseBoolean(args[5].toLowerCase());
        this.event = Boolean.parseBoolean(args[6].toLowerCase());
        this.score = Integer.parseInt(args[7]);
        this.type = TriggerType.valueOf(args[8].toUpperCase());
        if (!toastText.isEmpty()) {
            if (score != 0) toastText += " " + (score >= 0 ? "+" : "") + score + "pts";
            if (event) toastText += " +1ev";
        }
    }

    @Override
    public void trigger(Game game) {
        Player player = game.getEntitySystem().getPlayer();

        // Check if the trigger should be activated
        boolean isTouchTrigger = (type == TriggerType.TOUCH);
        boolean isInteractTrigger = (type == TriggerType.INTERACT && player.isInteract());

        // Stop the player from moving when a trigger is activated
        player.stopMoving();

        // If the conditions aren't met, do nothing
        if (!isTouchTrigger && !isInteractTrigger) {
            return;
        }

        DialogueBox dialogueBox = game.getUiSystem().getDialogueBox();
        // Only start the dialogue if it's not already showing
        if (!dialogueBox.isVisible()) {
            dialogueBox.showDialogue(dialogue);

            // Change the sprite of the object we're interacting with
            game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

            // If this is a story event and it's the first time interacting
            if (firstInteraction) {
                if (event) game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText, Color.ORANGE);
                game.getUiSystem().getStatusBar().addScore(score);

                // Set the flag so this block doesn't run again
                firstInteraction = false;

                // Make the object non-collidable if needed
                if (uncollidable) {
                    game.getEntitySystem().getEntities().get(id).setCollidable(false);
                }
            }
        }

        // If the object is meant to be destroyed, set it to inactive
        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setAlive(false);
        }

        // Reset the player's interact flag so it doesn't trigger again instantly
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
