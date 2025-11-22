package io.github.game.utils.interactions;

import io.github.game.Game;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;


public class DialogueTrigger implements Trigger {

    private final String id;
    private final String dialogue;
    private final boolean destroy;
    private final boolean event;
    private boolean firstInteraction = true;

    public DialogueTrigger(String id, int dialogueOption, boolean destroy, boolean event) {
        this.id = id;
        this.dialogue = DialogueLoader.getBlock(id, dialogueOption);
        this.destroy = destroy;
        this.event = event;
    }


    @Override
    public void trigger(Game game) {
        DialogueBox dialogueBox = game.getUiManager().getDialogueBox();
        game.getPlayer().stopMoving();
        if (!dialogueBox.isVisible()) {
            dialogueBox.showDialogue(dialogue);

            dialogueBox.showDialogue(
                DialogueLoader.getBlock(
                    "professor",
                    0)
            );

            if (event && firstInteraction) {
                game.getUiManager().getStatusBar().incrementEventCounter();
                firstInteraction = false;
            }
        }

        if (destroy) game.getEnemyManager().getEntities().get(id).setActive(false);
    }
}
