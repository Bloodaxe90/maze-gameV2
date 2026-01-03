package io.github.game.headless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.mock;

public class AbstractHeadlessGdxTest {
    @BeforeEach
    public void setup() {
        HeadlessLauncher.init();
        Gdx.gl = Gdx.gl20 = mock(GL20.class);
    }
}
