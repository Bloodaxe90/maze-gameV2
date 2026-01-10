package io.github.game.utils.triggers;

import com.badlogic.gdx.graphics.Color;
import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.utils.io.AudioPlayer;

/**
 * NEW CLASS: (explanation for why in Trigger class)
 *
 * A trigger that temporarily modifies the player's speed
 */
public class PlayerSpeedTrigger implements Trigger {

    private final String id;
    private final float speedMultiplier; // e.g. 2.0 for double speed, 0.5 for half speed
    private final float duration; // How long the effect lasts in seconds
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
    public PlayerSpeedTrigger(String[] args) {
        this.id = args[0];
        this.speedMultiplier = Float.parseFloat(args[1]);
        this.duration = Float.parseFloat(args[2]);
        // If the sprite name is "null" the sprite wont change
        this.interactionSprite = args[3].equalsIgnoreCase("null") ? "" : args[3];
        this.destroy = Boolean.parseBoolean(args[4].toLowerCase());
        this.uncollidable = Boolean.parseBoolean(args[5].toLowerCase());
        this.event = Boolean.parseBoolean(args[6].toLowerCase());
        this.score = Integer.parseInt(args[7]);
        this.type = TriggerType.valueOf(args[8].toUpperCase());
    }

    @Override
    public void trigger(Game game) {
        Player player = game.getEntitySystem().getPlayer();

        player.stopMoving();

        // Check if the trigger conditions are met
        boolean isTouchTrigger = (type == TriggerType.TOUCH);
        boolean isInteractTrigger = (type == TriggerType.INTERACT && player.isInteract());

        if (!isTouchTrigger && !isInteractTrigger) {
            return;
        }

        // Apply the speed modifier to the player
        player.applySpeedModifier(speedMultiplier, duration);

        // We can add a toast message to let the player know what happened
        String effect = "Slowed Down!";
        Color colour = Color.RED;
        if (speedMultiplier > 1.0f) {
            effect = "Speed Boost!";
            colour = Color.GREEN;
            AudioPlayer.playSound("powerup", 2f);
        } else if (speedMultiplier == 0) {
            effect = "Frozen!";
            colour = Color.CYAN;
            AudioPlayer.playSound("freeze", 2f);
        } else {
            AudioPlayer.playSound("debuff", 2f);
        }
        game.getUiSystem().getToastBar().addToast(effect + " " + duration + "s " + (score != 0 ? (score >= 0 ? "+" : "") + score + "pts" : "") + (event ? " +1ev" : ""), colour);
        game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);
        // If this is a story event and it's the first time interacting
        if (firstInteraction) {
            if (event) game.getUiSystem().getStatusBar().incrementEventCounter();
            game.getUiSystem().getStatusBar().addScore(score);

            // Set the flag so this block doesn't run again
            firstInteraction = false;

            // Make the object non-collidable if needed
            if (uncollidable) {
                game.getEntitySystem().getEntities().get(id).setCollidable(false);
            }
        }

        // If the trigger is a one-time use item, destroy it
        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setAlive(false);
        }

        // Reset the player's interact flag if needed
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
