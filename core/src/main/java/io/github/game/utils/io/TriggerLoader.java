package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;
import io.github.game.utils.triggers.Trigger;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TriggerLoader {

    private static final Map<String, Class<? extends Trigger>> triggerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("io.github.game.utils.triggers");

        Set<Class<? extends Trigger>> children = reflections.getSubTypesOf(Trigger.class);

        for (Class<? extends Trigger> child : children) {
            String key = child.getSimpleName().replace("Trigger", "");
            triggerMap.put(key, child);
        }
    }

    public static Trigger loadTrigger(String rawData) {
        if (rawData == null || rawData.isEmpty()) return null;

        String[] fields = rawData.split(",");

        String[] params = Arrays.copyOfRange(fields, 1, fields.length);

        Class<? extends Trigger> childClass = triggerMap.get(fields[0]);

        try {
            return childClass.getConstructor(String[].class).newInstance((Object) params);
        } catch (Exception e) {
            Gdx.app.log("ERROR", "Could not create trigger " + childClass.getSimpleName() + ": " + e);
            return null;
        }
    }
}
