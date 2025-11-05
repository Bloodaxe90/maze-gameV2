package io.github.eng1group9.systems;

import com.badlogic.gdx.Gdx;

public class TimerSystem {
    public float elapsedTime;
    private float timeTooAdd;
    private boolean paused = false;

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

    public void tick() {
        float delta = Gdx.graphics.getDeltaTime();
        elapsedTime += (delta * 1000) + getExtraTime(delta);
        if (getTimeLeft() <= 0) {
            ToastSystem.addToast("Time's up!");
        }
    }

    private float getExtraTime(float delta) {
        if (timeTooAdd <= 0) return 0;
        float change = delta * 25000;
        if (timeTooAdd < change) change = timeTooAdd;
        timeTooAdd -= change;
        return change;
    }

    public float getTimeLeft() {
        return 500 - (int)(elapsedTime / 1000);
    }
    
}
