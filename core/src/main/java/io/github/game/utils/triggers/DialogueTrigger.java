package io.github.game.utils.triggers;

import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;


public class DialogueTrigger implements Trigger {

    private final String id;
    private final String dialogue;
    private final String toastText;
    private final String interactionSprite;
    private final boolean destroy;
    private final boolean event;
    private final int score;
    private final TriggerType type;

    private boolean firstInteraction = true;

    public DialogueTrigger(String[] args) {
        this.id = args[0];
        this.dialogue = DialogueLoader.getBlock(id, Integer.parseInt(args[1]));
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[2]));
        this.interactionSprite = args[3].equalsIgnoreCase("null") ? "" : args[3];
        this.destroy = Boolean.parseBoolean(args[4]);
        this.event = Boolean.parseBoolean(args[5]);
        this.score = Integer.parseInt(args[6]);
        this.type = TriggerType.valueOf(args[7].toUpperCase());    }

    @Override
    public void trigger(Game game) {
        Player player = game.getEntitySystem().getPlayer();
        boolean isTouchTrigger = (type == TriggerType.TOUCH);
        boolean isInteractTrigger = (type == TriggerType.INTERACT && player.isInteract());
        player.stopMoving();

        if (!isTouchTrigger && !isInteractTrigger) {
            return;
        }
        DialogueBox dialogueBox = game.getUiSystem().getDialogueBox();
        if (!dialogueBox.isVisible()) {
            dialogueBox.showDialogue(dialogue);
            game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

            if (event && firstInteraction) {
                game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText);
                game.getUiSystem().getStatusBar().addScore(score);
                firstInteraction = false;
            }
        }
        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setActive(false);
        }
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
