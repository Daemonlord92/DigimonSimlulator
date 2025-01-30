package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Sector {
    private final String name;
    private final List<Digimon> digimons;
    private List<Sector> adjacentSectors;
    private final Grid grid;
    private static final int DEFAULT_GRID_SIZE = 20;

    public Sector(String name) {
        this.name = name;
        this.digimons = new ArrayList<>();
        this.adjacentSectors = new ArrayList<>();
        this.grid = new Grid(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE);
        initializeBorderCells();
    }

    private void initializeBorderCells() {
        // Mark cells on the edges as border cells
        for (int x = 0; x < DEFAULT_GRID_SIZE; x++) {
            grid.getCell(x, 0).setType(GridCell.CellType.BORDER);
            grid.getCell(x, DEFAULT_GRID_SIZE - 1).setType(GridCell.CellType.BORDER);
        }
        for (int y = 0; y < DEFAULT_GRID_SIZE; y++) {
            grid.getCell(0, y).setType(GridCell.CellType.BORDER);
            grid.getCell(DEFAULT_GRID_SIZE - 1, y).setType(GridCell.CellType.BORDER);
        }
    }

    public void addDigimon(Digimon digimon) {
        if (digimon == null) {
            throw new IllegalArgumentException("Cannot add null Digimon to sector");
        }

        // Find an empty cell for the Digimon
        Optional<GridCell> emptyCell = findEmptyCell();
        if (emptyCell.isPresent()) {
            GridCell cell = emptyCell.get();
            cell.setOccupant(digimon);
            digimons.add(digimon);
        } else {
            throw new IllegalStateException("No empty cells available in sector " + name);
        }
    }

    private Optional<GridCell> findEmptyCell() {
        for (int x = 1; x < DEFAULT_GRID_SIZE - 1; x++) {
            for (int y = 1; y < DEFAULT_GRID_SIZE - 1; y++) {
                GridCell cell = grid.getCell(x, y);
                if (cell.getOccupant() == null && cell.getBuilding() == null
                        && cell.getType() != GridCell.CellType.BLOCKED) {
                    return Optional.of(cell);
                }
            }
        }
        return Optional.empty();
    }

    public void removeDigimon(Digimon digimon) {
        // Find and clear the cell containing this Digimon
        for (int x = 0; x < DEFAULT_GRID_SIZE; x++) {
            for (int y = 0; y < DEFAULT_GRID_SIZE; y++) {
                GridCell cell = grid.getCell(x, y);
                if (digimon.equals(cell.getOccupant())) {
                    cell.setOccupant(null);
                    break;
                }
            }
        }
        digimons.remove(digimon);
    }

    public void addAdjacentSector(Sector sector) {
        if (!adjacentSectors.contains(sector)) {
            adjacentSectors.add(sector);
            sector.addAdjacentSector(this);
        }
    }

    public boolean moveDigimon(Digimon digimon, int newX, int newY) {
        // Check if the new position is valid
        if (newX < 0 || newX >= DEFAULT_GRID_SIZE || newY < 0 || newY >= DEFAULT_GRID_SIZE) {
            return false;
        }

        GridCell targetCell = grid.getCell(newX, newY);

        // Check if the target cell is available
        if (targetCell.getOccupant() != null || targetCell.getBuilding() != null
                || targetCell.getType() == GridCell.CellType.BLOCKED) {
            return false;
        }

        // Find the Digimon's current cell and clear it
        for (int x = 0; x < DEFAULT_GRID_SIZE; x++) {
            for (int y = 0; y < DEFAULT_GRID_SIZE; y++) {
                GridCell cell = grid.getCell(x, y);
                if (digimon.equals(cell.getOccupant())) {
                    cell.setOccupant(null);
                    targetCell.setOccupant(digimon);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean placeBuilding(Building building, int x, int y) {
        try {
            grid.placeBuilding(building);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Getters
    public String getName() { return name; }
    public List<Digimon> getDigimons() { return new ArrayList<>(digimons); }
    public List<Sector> getAdjacentSectors() { return adjacentSectors; }
    public Grid getGrid() { return grid; }

    @Override
    public String toString() {
        return name;
    }

    public GridCell getCellAt(int x, int y) {
        return grid.getCell(x, y);
    }

    public List<GridCell> findPath(int startX, int startY, int endX, int endY) {
        GridCell start = grid.getCell(startX, startY);
        GridCell end = grid.getCell(endX, endY);
        return PathFinder.findPath(grid, start, end);
    }
}