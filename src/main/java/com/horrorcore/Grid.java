package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Grid {
    private final GridCell[][] cells;
    private final int width;
    private final int height;
    private final List<Building> buildings = new ArrayList<>();

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new GridCell[width][height];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new GridCell(x, y,
                        (x == 0 || x == width-1 || y == 0 || y == height-1) ?
                                GridCell.CellType.BORDER : GridCell.CellType.NORMAL);
            }
        }
    }

    public void placeBuilding(Building building) {
        int x = building.getX();
        int y = building.getY();

        if (!isValidBuildingLocation(x, y, building.getType())) {
            throw new IllegalArgumentException("Invalid building location");
        }

        buildings.add(building);
        cells[x][y].setBuilding(building);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
        cells[building.getX()][building.getY()].setBuilding(null);
    }

    private boolean isValidBuildingLocation(int x, int y, Building.BuildingType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        if (cells[x][y].getBuilding() != null) return false;

        // Check radius around building location
        int radius = type.getRadius();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    if (cells[newX][newY].getBuilding() != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Navigation methods
    public List<GridCell> findPath(GridCell start, GridCell end) {
        return PathFinder.findPath(this, start, end);
    }

    public GridCell getCell(int x, int y) {
        return cells[x][y];
    }

    public List<Building> getTribeBuildings(Tribe tribe) {
        return buildings.stream()
                .filter(b -> b.getOwner().equals(tribe))
                .collect(Collectors.toList());
    }

    public void cleanupTribeBuildings(Tribe tribe) {
        List<Building> tribeBuildings = getTribeBuildings(tribe);
        tribeBuildings.forEach(this::removeBuilding);
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }
}