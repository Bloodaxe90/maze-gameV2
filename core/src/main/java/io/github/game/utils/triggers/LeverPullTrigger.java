package io.github.game.utils.triggers;

import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

/**
 * A more advanced trigger that can either give an item to the player,
 * or take an item from them
 */
public class LeverPullTrigger implements Trigger {

    private final String id;
    private final String toastText; // Toast message to show on first interaction
    private final String interactionSprite; // Sprite to change to on first interaction
    private final boolean event; // Should this increment the event counter
    private final int score; // The amount to increment the score by
    private final TriggerType type; // If the trigger should be triggered on interaction (pressing E) or on touch
    private final String triggerableID; // The ID of the triggerable which this lever is controlled by.
    private boolean firstInteraction = true;

    /**
     * Constructor that parses trigger data from a string array
     * @param args The parameters for the trigger, loaded from the Tiled map
     */
    public LeverPullTrigger(String[] args) {
        this.id = args[0];
        // The dialogue text is loaded from a file using an index from the args (-1 for no text)
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[1]));
        // If the sprite name is "null" the sprite wont change
        this.interactionSprite = args[2].equalsIgnoreCase("null") ? "" : args[6];
        this.event = Boolean.parseBoolean(args[3].toLowerCase());
        this.score = Integer.parseInt(args[4]);
        this.type = TriggerType.valueOf(args[5].toUpperCase());
        this.triggerableID = args[6].toLowerCase();
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

        // If this is a story event and it's the first time interacting

        // Disable the spikes


        // Change sprite once pulled
        game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

        // Reset the player's interact flag
        if (isInteractTrigger) {
            player.setInteract(false);
        }

            if (event && firstInteraction) {
                game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText);
                game.getUiSystem().getStatusBar().addScore(score);

                // Set the flag so this block doesn't run again
                firstInteraction = false;
            }
    }
}
