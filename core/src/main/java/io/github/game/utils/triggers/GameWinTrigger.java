package io.github.game.utils.triggers;

import io.github.game.Game;
import io.github.game.entity.entities.Player;
import io.github.game.ui.elements.DialogueBox;
import io.github.game.utils.io.DialogueLoader;

public class GameWinTrigger implements Trigger {

    private final String id;
    private final String item;
    private final String dialogueNoKeycard;
    private final boolean destroy;
    private final boolean event;
    private boolean firstInteraction = true;
    private enum TriggerType{TOUCH, INTERACT};
    private final TriggerType type;

    public GameWinTrigger(String[] args) {
        this.id = args[0];
        this.item = args[1];
        this.dialogueNoKeycard = DialogueLoader.getBlock(id, Integer.parseInt(args[2]));
        this.destroy = Boolean.parseBoolean(args[3]);
        this.event = Boolean.parseBoolean(args[4]);
        this.type = TriggerType.valueOf(args[5].toUpperCase());
    }

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
            if (event && firstInteraction) {
                game.getUiSystem().getStatusBar().incrementEventCounter();
                firstInteraction = false;
            }
            game.getUiSystem().setupGameOverScreen("Win\nYou made it home in time");
        } else {
            dialogueBox.showDialogue(dialogueNoKeycard);
        }

        if (destroy) {
            game.getEntitySystem().getEntities().get(id).setActive(false);
        }
        if (isInteractTrigger) {
            player.setInteract(false);
        }
    }
}
