package com.horrorcore;

public class Building {
    private final BuildingType type;
    private final Tribe owner;
    private final int x;
    private final int y;

    public enum BuildingType {
        HOUSE(2),
        FARM(3),
        BARRACKS(4),
        CITY_CENTER(5);

        private final int radius;
        BuildingType(int radius) { this.radius = radius; }
        public int getRadius() { return radius; }
    }

    public Building(BuildingType type, Tribe owner, int x, int y) {
        this.type = type;
        this.owner = owner;
        this.x = x;
        this.y = y;
    }

    // Getters
    public BuildingType getType() { return type; }
    public Tribe getOwner() { return owner; }
    public int getX() { return x; }
    public int getY() { return y; }
}
