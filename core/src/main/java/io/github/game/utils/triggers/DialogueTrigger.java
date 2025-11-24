package io.github.game.utils.triggers;

import com.badlogic.gdx.Gdx;
import io.github.game.Game;
import io.github.game.entities.Entity;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;


public class DialogueTrigger implements Trigger {

    private final String id;
    private final String dialogue;
    private final boolean destroy;
    private final boolean event;
    private boolean firstInteraction = true;

    public DialogueTrigger(String[] args) {
        this.id = args[0];
        Gdx.app.log("0", id);

        this.dialogue = DialogueLoader.getBlock(id, Integer.parseInt(args[1]));

        this.destroy = Boolean.parseBoolean(args[2]);
        this.event = Boolean.parseBoolean(args[3]);

    }


    @Override
    public void trigger(Game game) {
        DialogueBox dialogueBox = game.getUiManager().getDialogueBox();
        game.getEntityManager().getPlayer().stopMoving();
        if (!dialogueBox.isVisible()) {
            dialogueBox.showDialogue(dialogue);

            if (event && firstInteraction) {
                game.getUiManager().getStatusBar().incrementEventCounter();
                firstInteraction = false;
            }
        }

        if (destroy) game.getEntityManager().getEntities().get(id).setActive(false);
    }
}
