package com.horrorcore.grid;

import com.horrorcore.entity.Building;
import com.horrorcore.entity.Digimon;

public class GridCell {
    private Digimon occupant;
    private Building building;
    private final int x;
    private final int y;
    private CellType type;

    public void setType(CellType cellType) {
        this.type = cellType;
    }

    public enum CellType {
        NORMAL,
        BORDER,  // Cells that connect to other sectors
        BLOCKED  // Impassable terrain
    }

    public GridCell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // Getters/Setters
    public Digimon getOccupant() { return occupant; }
    public void setOccupant(Digimon occupant) { this.occupant = occupant; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
    public int getX() { return x; }
    public int getY() { return y; }
    public CellType getType() { return type; }
}
