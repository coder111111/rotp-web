package rotp.tea;

public class Empire {
    public static final int SHAPE_CIRCLE = 0;
    public static final int SHAPE_SQUARE = 1;
    public static final int SHAPE_DIAMOND = 2;
    public static final int SHAPE_TRIANGLE1 = 3;
    public static final int SHAPE_TRIANGLE2 = 4;

    private final int id;
    private boolean isPlayer;
    private String name;
    private int shape;
    private float shipRange;
    private float scoutRange;
    private AlphaColor color;
    private AlphaColor ownershipColor;
    private AlphaColor empireRangeColor;
    private AlphaColor shipBorderColor;
    private AlphaColor scoutBorderColor;

    public Empire(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public boolean isIsPlayer() {
        return isPlayer;
    }

    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public float getShipRange() {
        return shipRange;
    }

    public void setShipRange(float shipRange) {
        this.shipRange = shipRange;
    }

    public float getScoutRange() {
        return scoutRange;
    }

    public void setScoutRange(float scoutRange) {
        this.scoutRange = scoutRange;
    }

    public AlphaColor getColor() {
        return color;
    }

    public void setColor(AlphaColor color) {
        this.color = color;
    }

    public AlphaColor getOwnershipColor() {
        return ownershipColor;
    }

    public void setOwnershipColor(AlphaColor ownershipColor) {
        this.ownershipColor = ownershipColor;
    }

    public AlphaColor getEmpireRangeColor() {
        return empireRangeColor;
    }

    public void setEmpireRangeColor(AlphaColor empireRangeColor) {
        this.empireRangeColor = empireRangeColor;
    }

    public AlphaColor getShipBorderColor() {
        return shipBorderColor;
    }

    public void setShipBorderColor(AlphaColor shipBorderColor) {
        this.shipBorderColor = shipBorderColor;
    }

    public AlphaColor getScoutBorderColor() {
        return scoutBorderColor;
    }

    public void setScoutBorderColor(AlphaColor scoutBorderColor) {
        this.scoutBorderColor = scoutBorderColor;
    }

    @Override
    public String toString() {
        return "Empire{" + "id=" + id + ", isPlayer=" + isPlayer + ", name=" + name + ", shape=" + shape + ", shipRange=" + shipRange + ", scoutRange=" + scoutRange + ", ownershipColor=" + ownershipColor + ", empireRangeColor=" + empireRangeColor + ", shipBorderColor=" + shipBorderColor + ", scoutBorderColor=" + scoutBorderColor + '}';
    }
    
}
