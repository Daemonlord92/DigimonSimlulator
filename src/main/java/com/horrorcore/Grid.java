package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Grid {
    private final GridCell[][] cells;
    private final int width;
    private final int height;
    private final List<Building> buildings = new ArrayList<>();
    private static final double BLOCKED_CELL_PROBABILITY = 0.15; // 15% chance for a cell to be blocked
    private static final int MIN_PATH_WIDTH = 2;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new GridCell[width][height];
        initializeGrid();
    }

    private void initializeGrid() {
        Random random = new Random();

        // First, initialize all cells as NORMAL
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new GridCell(x, y, GridCell.CellType.NORMAL);
            }
        }

        // Set border cells
        for (int x = 0; x < width; x++) {
            cells[x][0].setType(GridCell.CellType.BORDER);
            cells[x][height - 1].setType(GridCell.CellType.BORDER);
        }
        for (int y = 0; y < height; y++) {
            cells[0][y].setType(GridCell.CellType.BORDER);
            cells[width - 1][y].setType(GridCell.CellType.BORDER);
        }

        // Randomly place blocked cells while ensuring connectivity
        for (int x = MIN_PATH_WIDTH; x < width - MIN_PATH_WIDTH; x++) {
            for (int y = MIN_PATH_WIDTH; y < height - MIN_PATH_WIDTH; y++) {
                if (random.nextDouble() < BLOCKED_CELL_PROBABILITY) {
                    // Check if blocking this cell would create an isolated area
                    boolean canBlock = true;

                    // Check if there's enough space around the cell
                    for (int dx = -MIN_PATH_WIDTH + 1; dx < MIN_PATH_WIDTH; dx++) {
                        for (int dy = -MIN_PATH_WIDTH + 1; dy < MIN_PATH_WIDTH; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                if (cells[nx][ny].getType() == GridCell.CellType.BLOCKED) {
                                    canBlock = false;
                                    break;
                                }
                            }
                        }
                        if (!canBlock) break;
                    }

                    if (canBlock) {
                        cells[x][y].setType(GridCell.CellType.BLOCKED);
                    }
                }
            }
        }

        // Ensure there's always a path between any two points
        ensureConnectivity();
    }

    private void ensureConnectivity() {
        // Simple flood fill to check connectivity
        boolean[][] visited = new boolean[width][height];
        Point start = findFirstAccessiblePoint();
        floodFill(start.x, start.y, visited);

        // If any accessible cell wasn't visited, clear blocked cells to create a path
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!visited[x][y] && cells[x][y].getType() == GridCell.CellType.NORMAL) {
                    createPathToUnvisited(x, y);
                }
            }
        }
    }

    private Point findFirstAccessiblePoint() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y].getType() == GridCell.CellType.NORMAL) {
                    return new Point(x, y);
                }
            }
        }
        return new Point(1, 1); // Fallback
    }

    private void floodFill(int x, int y, boolean[][] visited) {
        if (x < 0 || x >= width || y < 0 || y >= height ||
                visited[x][y] || cells[x][y].getType() == GridCell.CellType.BLOCKED) {
            return;
        }

        visited[x][y] = true;
        floodFill(x + 1, y, visited);
        floodFill(x - 1, y, visited);
        floodFill(x, y + 1, visited);
        floodFill(x, y - 1, visited);
    }

    private void createPathToUnvisited(int targetX, int targetY) {
        // Simple implementation: clear a straight path to the nearest accessible cell
        Point start = findFirstAccessiblePoint();
        int x = start.x;
        int y = start.y;

        while (x != targetX) {
            x += (x < targetX) ? 1 : -1;
            cells[x][y].setType(GridCell.CellType.NORMAL);
        }
        while (y != targetY) {
            y += (y < targetY) ? 1 : -1;
            cells[x][y].setType(GridCell.CellType.NORMAL);
        }
    }

    // Existing methods remain the same...

    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
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