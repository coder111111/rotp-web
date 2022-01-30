package rotp.tea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Galaxy {
    List<StarSystem> stars = new ArrayList<>();
    Map<Integer, Empire> empires = new HashMap<>();
    private float minX = -1;
    private float maxX = -1;
    private float minY = -1;
    private float maxY = -1;

    void addStarSystem(StarSystem ss) {
        if (minX < 0 || ss.getX() < minX) {
            minX = ss.getX();
        }
        if (maxX < 0 || ss.getX() > maxX) {
            maxX = ss.getX();
        }
        if (minY < 0 || ss.getY() < minY) {
            minY = ss.getY();
        }
        if (maxY < 0 || ss.getY() > maxY) {
            maxY = ss.getY();
        }
        stars.add(ss);
    }
    void addEmpire(Empire ee) {
        empires.put(ee.getId(), ee);
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public List<StarSystem> getStars() {
        return stars;
    }
    
    public Empire getEmpire(int id) {
        return empires.get(id);
    }
    public Empire getPlayer() {
        for (Empire e: empires.values()) {
            if (e.isIsPlayer()) {
                return e;
            }
        }
        return null;
    }
}
