package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.ui.elements.Hotbar;
import io.github.game.ui.elements.PauseMenu;
import io.github.game.ui.elements.StatusBar;
import io.github.game.ui.elements.ToastBar;


public class UiSystem {
    private final FitViewport uiViewport;

    private final Stage stage;
    private final Skin skin;
    private final DialogueBox dialogueBox;
    private final Hotbar hotbar;
    private final PauseMenu pauseMenu;
    private final StatusBar statusBar;
    private final ToastBar toastBar;
    private final TextureAtlas uiAtlas;

    public UiSystem(String layerName) {
        this.uiViewport = new FitViewport(Game.WORLD_SIZE.x, Game.WORLD_SIZE.y);
        this.stage = new Stage(uiViewport);

        this.uiAtlas = new TextureAtlas("assets/atlas/" + layerName + ".atlas");
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        this.dialogueBox = new DialogueBox("dialogue", layerName, this.skin, uiAtlas);
        this.dialogueBox.setVisible(false);
        this.stage.addActor(this.dialogueBox);

        this.pauseMenu = new PauseMenu("pause_menu", layerName, skin);
        this.pauseMenu.setVisible(false);
        this.stage.addActor(pauseMenu);

        this.hotbar = new Hotbar("hotbar", layerName, skin, uiAtlas);
        this.stage.addActor(this.hotbar);

        this.statusBar = new StatusBar("status_bar", layerName, skin);
        this.stage.addActor(statusBar);

        this.toastBar = new ToastBar("toast_bar", layerName, skin);
        this.stage.addActor(toastBar);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this.stage);
        Gdx.input.setInputProcessor(multiplexer);
    }


    public void update(float delta, boolean playing, Player player) {
        if (playing) {
            stage.act(delta);
            hotbar.updateInventory(player.getInventory());
            statusBar.update(delta);
        }

        if (statusBar.isTimeUp()) {
            playing = false;
            setupGameOverScreen("Loss\nYou timed out");
        }

        pauseMenu.setVisible(!playing);     }


    public void setupGameOverScreen(String text) {
        statusBar.update(0);
        pauseMenu.setText("Game Over: " + text + "\n\n" + statusBar.getStatusText());
        statusBar.setVisible(false);
    }


    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }


    public void dispose() {
        stage.dispose();
        skin.dispose();
        uiAtlas.dispose();
    }


    public StatusBar getStatusBar() {
        return statusBar;
    }


    public Stage getStage() {
        return stage;
    }


    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }


    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }


    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    public ToastBar getToastBar() {
        return toastBar;
    }

    public FitViewport getViewport() {
        return uiViewport;
    }
}
