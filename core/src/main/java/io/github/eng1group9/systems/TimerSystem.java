package io.github.eng1group9.systems;

import java.sql.Date;

import com.badlogic.gdx.Gdx;

import io.github.eng1group9.Main;

/**
 * System used to keep track of how much time the player has left.
 */
public class TimerSystem {
    public static float elapsedTime = 0;// time passed in seconds;
    private float timeTooAdd;
    private static final float TIMESTARTVALUE = 300;

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
        elapsedTime += delta + getExtraTime(delta);
        if (getTimeLeft() <= 0) {
            Main.LoseGame();
        }
    }

    /**
     * @return How much time should be added to the timer as a result of the addGradually method. 
     */
    private float getExtraTime(float delta) {
        if (timeTooAdd <= 0) return 0;
        float change = delta * 25;
        if (timeTooAdd < change) change = timeTooAdd;
        timeTooAdd -= change;
        return change;
    }

    /**
     * @return How much time the player has left to escape.
     */
    public static int getTimeLeft() {
        return (int)(TIMESTARTVALUE - elapsedTime);
    }

    public static String getClockDisplay() {
        int timeLeft = getTimeLeft();
        System.out.println(timeLeft / 60);
        String mins = getMinsDisplay(timeLeft);
        String secs = getSecsDisplay(timeLeft);
        return "Time Left: " + mins + ":" + secs;
        
    }
    
    private static String getMinsDisplay(int seconds) {
        return Integer.toString(getMins(seconds));
    }

    private static int getMins(int seconds) {
        return Math.floorDiv(seconds, 60);
    }

    private static String getSecsDisplay(int timeLeft) {
        int secsValue = getSecs(timeLeft);
        if (secsValue < 10) return "0" + Integer.toString(secsValue);
        return Integer.toString(secsValue);
    }

    private static int getSecs(int timeLeft) {
        return timeLeft - (getMins(timeLeft) * 60);
    }
}
