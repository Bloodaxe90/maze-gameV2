package io.github.game.utils.triggers;

import io.github.game.Game;

// TODO need to add inheritance to other triggers somehow, too much repeated code in things that inherit Trigger
public interface Trigger {

    void trigger(Game game);
}
