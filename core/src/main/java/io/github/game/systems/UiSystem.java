package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.*;

import static io.github.game.Game.PLAYING;

/**
 * NEW CLASS: (explanation for why in ToastBar and StatusBar)
 *
 * Manages all the UI elements, the stage they live on, and their logic
 */
public class UiSystem {
    private final FitViewport uiViewport;

    private final Stage stage;
    private final Skin skin;
    private final DialogueBox dialogueBox;
    private final Hotbar hotbar;
    private final PauseMenu pauseMenu;
    private final StatusBar statusBar;
    private final Leaderboard leaderboard;
    private final ToastBar toastBar;
    private final NameInput nameInput;
    private final TextureAtlas uiAtlas;

    /**
     * Constructor for the UI System
     * @param layerName The name of the layer in the Tiled map where UI elements are defined
     */
    public UiSystem(String layerName) {
        // Create a viewport that will scale our UI to a 960x640 resolution
        this.uiViewport = new FitViewport(960, 640);
        this.stage = new Stage(uiViewport);

        // Load the textures and styles for the UI
        this.uiAtlas = new TextureAtlas("assets/atlas/" + layerName + ".atlas");
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create all the individual UI elements
        this.dialogueBox = new DialogueBox("dialogue", layerName, uiViewport, this.skin, uiAtlas);
        this.dialogueBox.setVisible(false); // Hide it until its needed
        this.stage.addActor(this.dialogueBox);

        this.pauseMenu = new PauseMenu("pause_menu", layerName, uiViewport, skin);
        this.pauseMenu.setVisible(false);
        this.stage.addActor(pauseMenu);

        this.hotbar = new Hotbar("hotbar", layerName, uiViewport, skin, uiAtlas);
        this.stage.addActor(this.hotbar);

        this.statusBar = new StatusBar("status_bar", layerName, uiViewport, skin);
        this.stage.addActor(statusBar);

        this.leaderboard = new Leaderboard("leaderboard", layerName, uiViewport, skin);
        this.leaderboard.setVisible(false);

        this.stage.addActor(leaderboard);

        this.toastBar = new ToastBar("toast_bar", layerName, uiViewport, skin);
        this.stage.addActor(toastBar);

        this.nameInput = new NameInput("name_input", layerName, uiViewport, skin);
        this.stage.addActor(nameInput);

        // An InputMultiplexer allows both the UI stage and other things (like the player)
        // to receive input events
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this.stage); // The stage gets priority
        Gdx.input.setInputProcessor(multiplexer);
    }


    /**
     * Updates all the UI elements each frame
     * @param delta Time since last frame
     * @param player The player object, to get data like inventory and health
     */
    public void update(float delta, Player player) {
        // Only update certain elements if the game is not paused
        if (PLAYING) {
            stage.act(delta); // This calls the 'act' method on all actors in the stage
            hotbar.updateInventory(player.getInventory());

            statusBar.update(delta);

            if (statusBar.isTimeUp()) {
                setupGameOverScreen("Loss\nYou timed out\nThe game is not designed to be beaten first go,\n but it can be beaten in under 5 mins.\nTry again and pay attention to ALL the dialogue");
            }
            stage.setKeyboardFocus(null);
        } else {
            stage.setKeyboardFocus(nameInput.getNameTextBox());
        }

        // The pause menu and leaderboard should only be visible when the game is NOT playing
        pauseMenu.setVisible(!PLAYING);
        leaderboard.setVisible(!PLAYING);
        nameInput.setVisible(!PLAYING);

    }


    /**
     * Sets up the text and visibility for the Game Over screen
     * @param text The reason for the game over
     */
    public void setupGameOverScreen(String text) {
        nameInput.getNameTextBox().setDisabled(true);
        leaderboard.save(nameInput.getName(), statusBar.getScore());
        leaderboard.update();
        statusBar.update(0); // Update status bar one last time
        pauseMenu.setText("Game Over: " + text + "\n\n" + statusBar.getStatusText());
        statusBar.setVisible(false); // Hide the normal status bar
    }


    /**
     * Draws all the UI elements
     */
    public void render() {
        stage.draw();
    }

    /**
     * Updates the UI viewport when the game window is resized
     * @param width The new window width
     * @param height The new window height
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }


    /**
     * Cleans up assets
     */
    public void dispose() {
        stage.dispose();
        skin.dispose();
        leaderboard.save(nameInput.getName(), statusBar.getScore()); // Save the leaderboard before the game closes
        uiAtlas.dispose();
    }


    /** @return The status bar element */
    public StatusBar getStatusBar() {
        return statusBar;
    }


    /** @return The main UI stage */
    public Stage getStage() {
        return stage;
    }


    /** @return The dialogue box element */
    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }


    /** @return The pause menu element */
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }


    /** @return The texture atlas for the UI */
    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    /** @return The toast bar element */
    public ToastBar getToastBar() {
        return toastBar;
    }

    /** @return The UI viewport */
    public FitViewport getViewport() {
        return uiViewport;
    }

    /** @return The name input element */
    public NameInput getNameInput() {
        return nameInput;
    }
}
