package io.github.eng1group9.systems;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;

import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Player;

/**
 * The system used to make things happen if a player enters a given area.
 * The Triggers layer on the TileMap contains Rectangles used to denote these areas.
 * The name of the Rectangle is in the form "ID,triggerType" 
 * which is used to determine what happens when it is triggered, and how.
 * 
 * triggerType must be in the form T or I
 * for Touch or Interact
 */
public class TriggerSystem {

    /**
     * A Trigger with a zone and a player it relates too. 
     */
    static class Trigger {
        private int ID;
        private Rectangle zone;
        private boolean activateOnTouch = false;

        public Trigger(int ID, boolean activateOnTouch, Rectangle zone) {
            this.ID = ID;
            this.activateOnTouch = activateOnTouch;
            this.zone = zone;
        }

        public int getID() {
            return ID;
        }

        public boolean isActivateOnTouch() {
            return activateOnTouch;
        }

        public boolean isActivateOnInteract() {
            return !activateOnTouch;
        }

        public Rectangle getZone() {
            return zone;
        }

        public boolean playerInZone(Player player) {
            return player.isColliding(zone);
        }
    }

    private static List<Trigger> touchTriggers = new LinkedList<>();
    private static List<Trigger> interactTriggers = new LinkedList<>();

    public static void init(String tmxPath) {
        List<Trigger> triggers = getTriggers(tmxPath);
        for (Trigger t : triggers) {
            if (t.isActivateOnTouch()) {
                touchTriggers.add(t);
            }
            else {
                interactTriggers.add(t);
            }
        }
    }

    public static List<Trigger> getTriggers(String tmxPath) {
        TiledMap map = new TmxMapLoader().load(tmxPath);
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        MapObjects triggerObjects = triggerLayer.getObjects();
        List<Trigger> triggers = new LinkedList<>();

        for (MapObject mapObject : triggerObjects) {
            RectangleMapObject recMapObj = (RectangleMapObject) mapObject;
            int ID = Integer.parseInt(recMapObj.getName().split(",")[0]);

            Rectangle zone = recMapObj.getRectangle();
            zone.set(zone.x * 2, zone.y * 2, zone.width * 2, zone.height * 2);

            String triggerType = recMapObj.getName().split(",")[1];
            Trigger t = new Trigger(ID, triggerType.equals("T"), zone);
            triggers.add(t);
            System.out.println("Loaded Trigger " + t.getID());
        }
        return triggers;
    }

    public static List<Trigger> getTouchTriggers() {
        return touchTriggers;
    }

    public static List<Trigger> getInteractTriggers() {
        return interactTriggers;
    }

    public static boolean remove(int ID) {
        for (Trigger t : touchTriggers) {
            if (t.getID() == ID) {
                touchTriggers.remove(t);
                return true;
            }
        }
        for (Trigger t : interactTriggers) {
            if (t.getID() == ID) {
                interactTriggers.remove(t);
                return true;
            }
        }
        return false;
    }

    public static List<Trigger> getTriggers() {
        List<Trigger> triggers = new LinkedList<>();
        triggers.addAll(touchTriggers);
        triggers.addAll(interactTriggers);
        return triggers;
    }

    /**
     * Will check if the given player is staninding in any triggers, and trigger them if so.
     * @param player The player which is being checked.
     * This is run each time the player presses E.
     */
    public static void checkInteractTriggers(Player player) {
        for (Trigger t : interactTriggers) {
            if (t.playerInZone(player)) {
                System.out.println("Triggered " + t.getID() + "!");
                trigger(t.getID(), player);
            }
        }
    }

    /**
     * Will check if the given player is staninding in any triggers, and trigger them if so.
     * @param player The player which is being checked.
     * This is run each frame.
     */
    public static void checkTouchTriggers(Player player) {
        for (Trigger t : touchTriggers) {
            if (t.playerInZone(player)) {
                trigger(t.getID(), player);
            }
        }
    }

    /**
     * Will act based on which trigger has been activated
     * @param ID the trigger that has been activated
     */
    public static void trigger(int ID, Player player) {
        switch (ID) {
            case 0: // open the main door
                Main.winGame();
                break;
            case 1: // Get the chest room key
                player.giveChestRoomKey();
                break;
            case 2: // Get the scroll
                Main.getScroll();
                break;
            case 3: // Standing by the switch 
                Main.dropSpikes();
                break;
            case 4: // Standing by the mouse hole
                
                break;
            case 5: // Standing by the chest
                player.giveExitKey();
                break;
            case 6: // Standing by the chest room door
                Main.openChestRoomDoor();
                break;
            case 7: // Standing by the chest room door
                Main.openExit();
                break;
            default:
                break;
        }
    }
}
