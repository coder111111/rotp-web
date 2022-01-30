package rotp.tea;

import rotp.model.galaxy.StarType;

public class StarSystem {
    private final int id;
    private float x, y;
    private int empireId;
//    private Planet planet;
    private StarType starType;
    private String name = "";

    public StarSystem(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getEmpireId() {
        return empireId;
    }

    public void setEmpireId(int empireId) {
        this.empireId = empireId;
    }
    
    public StarType getStarType() {
        return starType;
    }

    public void setStarType(StarType starType) {
        this.starType = starType;
    }

    @Override
    public String toString() {
        return "StarSystem{" + "id=" + id + ", x=" + x + ", y=" + y + ", empireId=" + empireId + ", starType=" + starType + ", name=" + name + '}';
    }
    
}
