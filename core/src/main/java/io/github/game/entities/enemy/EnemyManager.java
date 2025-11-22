package io.github.game.entities.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import io.github.game.Game;
import io.github.game.entities.EntityManager;
import io.github.game.utils.interactions.DialogueTrigger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EnemyManager extends EntityManager {

    public EnemyManager(TextureAtlas spriteAtlas) {
        super(spriteAtlas);

        entities.put("professor", new Enemy("professor",
            "Entity",
            new DialogueTrigger("professor" , 0, true, true),
            3,
            100,
            spriteAtlas));

    }
}
