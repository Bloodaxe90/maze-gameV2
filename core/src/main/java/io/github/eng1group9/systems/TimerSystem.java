package io.github.eng1group9.systems;

import com.badlogic.gdx.Gdx;

import io.github.eng1group9.Main;

/**
 * System used to keep track of how much time the player has left.
 */
public class TimerSystem {
    public float elapsedTime;
    private float timeTooAdd;

    public TimerSystem() {
        elapsedTime = 0;
    }

    public void add(float amount) {
        elapsedTime += amount;
    }

    /**
     * Add to the timer, so that it goes up by this amount over time
     * @param amount
     */
    public void addGradually(float amount) {
        timeTooAdd += amount;
    }

    /**
     * Update the timer.
     */
    public void tick() {
        float delta = Gdx.graphics.getDeltaTime();
        elapsedTime += (delta * 1000) + getExtraTime(delta);
        if (getTimeLeft() <= 0) {
            Main.LoseGame();
        }
    }

    /**
     * @return How much time should be added to the timer as a result of the addGradually method. 
     */
    private float getExtraTime(float delta) {
        if (timeTooAdd <= 0) return 0;
        float change = delta * 25000;
        if (timeTooAdd < change) change = timeTooAdd;
        timeTooAdd -= change;
        return change;
    }

    /**
     * @return How much time the player has left to escape.
     */
    public float getTimeLeft() {
        return 500 - (int)(elapsedTime / 1000);
    }
    
}
