package io.github.game.utils.triggers;

import io.github.game.Game;

// TODO need to add inheritance to other triggers somehow, too much repeated code in things that inherit Trigger

/**
 * An interface for all triggerable events in the game
 */
public interface Trigger {

    // An enum to define the different ways a trigger can be activated
    enum TriggerType{TOUCH, INTERACT};

    /**
     * This method contains the logic that runs when the trigger is activated
     * @param game A reference to the main game class
     */
    void trigger(Game game);
}
