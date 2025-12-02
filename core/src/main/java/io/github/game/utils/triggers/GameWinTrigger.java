package io.github.game.utils.triggers;

import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

public class GameWinTrigger implements Trigger {

    private final String id;
    private final String item;
    private final String dialogueDefault;
    private final String toastText;
    private final String interactionSprite;
    private final boolean destroy;
    private final boolean event;
    private final int score;
    private boolean firstInteraction = true;
    private final TriggerType type;

    public GameWinTrigger(String[] args) {
        this.id = args[0];
        this.item = args[1];
        this.dialogueDefault = DialogueLoader.getBlock(id, Integer.parseInt(args[2]));
        this.toastText = DialogueLoader.getBlock(id, Integer.parseInt(args[3]));
        this.interactionSprite = args[4].equalsIgnoreCase("null") ? "" : args[4];
        this.destroy = Boolean.parseBoolean(args[5]);
        this.event = Boolean.parseBoolean(args[6]);
        this.score = Integer.parseInt(args[7]);
        this.type = TriggerType.valueOf(args[8].toUpperCase());    }

    @Override
    public void trigger(Game game) {
        DialogueBox dialogueBox = game.getUiSystem().getDialogueBox();
        Player player = game.getEntitySystem().getPlayer();
        boolean isTouchTrigger = (type == TriggerType.TOUCH);
        boolean isInteractTrigger = (type == TriggerType.INTERACT && player.isInteract());
        player.stopMoving();

        if (!isTouchTrigger && !isInteractTrigger) {
            return;
        }
        if (player.hasItem(item)) {
            game.getEntitySystem().getEntities().get(id).setSprite(interactionSprite);

            if (event && firstInteraction) {
                game.getUiSystem().getStatusBar().incrementEventCounter();
                game.getUiSystem().getToastBar().addToast(toastText);
                game.getUiSystem().getStatusBar().addScore(score);
                firstInteraction = false;
            }
            game.getUiSystem().setupGameOverScreen("Win\nYou made it home in time");
        } else {
            dialogueBox.showDialogue(dialogueDefault);
        }

        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setActive(false);
        }
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
