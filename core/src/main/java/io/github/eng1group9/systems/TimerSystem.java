package io.github.eng1group9.systems;

public class TimerSystem {
    public long elapsedTime;

    public TimerSystem() {
        elapsedTime = 0;
    }

    public void update(float delta) {
        elapsedTime += (long) (delta * 1000);
    }

    public void tick() {
        if (500 - (int)(elapsedTime / 1000) <= 0) {
            ToastSystem.addToast("Time's up!");
        }
    }
}
