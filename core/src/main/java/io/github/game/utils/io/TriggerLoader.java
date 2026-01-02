package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;
import io.github.game.utils.triggers.Trigger;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A utility class that uses reflection to load and create Trigger objects
 * This allows us to define triggers in the Tiled map editor as simple strings
 */
public final class TriggerLoader {

    // This map stores a mapping from a triggers name to its class file
    private static final Map<String, Class<? extends Trigger>> triggerMap = new HashMap<>();

    // This is a static block, which runs only once when the class is first loaded
    // We use it to automatically find all our trigger classes
    static {
        // The 'Reflections' library scans our project for classes
        Reflections reflections = new Reflections("io.github.game.utils.triggers");

        // Find all classes that are children of our base 'Trigger' class
        Set<Class<? extends Trigger>> children = reflections.getSubTypesOf(Trigger.class);

        // Loop through all the trigger classes we found
        for (Class<? extends Trigger> child : children) {
            // Create a simple key for each trigger
            String key = child.getSimpleName().replace("Trigger", "");
            triggerMap.put(key, child);
        }
    }

    /**
     * Creates a new instance of a Trigger class based on a raw string from Tiled
     *
     * @param rawData The string from the map's custom properties i.e. "Dialogue,some_id,1,true"
     * @return A new Trigger object, or null if it fails
     */
    public static Trigger loadTrigger(String rawData) {
        if (rawData == null || rawData.isEmpty()) return null;

        // Split the string by commas to get the trigger type and its parameters
        String[] fields = rawData.split(",");

        // The rest of the fields are the parameters for the trigger's constructor
        String[] params = Arrays.copyOfRange(fields, 1, fields.length);

        // The first field is the key we use to find the correct class in our map
        Class<? extends Trigger> childClass = triggerMap.get(fields[0]);

        try {
            // This is the reflection part
            // It finds the constructor that takes a String array and creates a new instance
            return childClass.getConstructor(String[].class).newInstance((Object) params);
        } catch (Exception e) {
            // Log an error if the class couldn't be created
            Gdx.app.log("ERROR", "Could not create trigger " + childClass.getSimpleName() + ": " + e);
            return null;
        }
    }
}
