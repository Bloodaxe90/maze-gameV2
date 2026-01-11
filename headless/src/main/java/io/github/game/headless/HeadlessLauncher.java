package io.github.game.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import io.github.game.Game;

public class HeadlessLauncher {
    public static void init() {
        createApplication(new ApplicationAdapter() {});
    }

    private static Application createApplication(ApplicationListener listener) {
        return new HeadlessApplication(listener, getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = -1;
        return configuration;
    }
}
