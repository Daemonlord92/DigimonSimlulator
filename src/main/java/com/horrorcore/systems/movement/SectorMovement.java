package com.horrorcore.systems.movement;

import com.horrorcore.systems.events.SimulationEvent;
import com.horrorcore.systems.events.SimulationSubject;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;
import com.horrorcore.grid.Grid;
import com.horrorcore.grid.GridCell;

import java.util.*;

public class SectorMovement {
    private static final int MAX_PATH_ATTEMPTS = 5;
    private static final int MOVEMENT_RADIUS = 5; // Maximum distance for random movement

    public static boolean moveDigimon(Digimon digimon, Sector currentSector, Random random) {
        GridCell currentCell = findDigimonCell(digimon, currentSector);
        if (currentCell == null) {
            return false;
        }

        // If Digimon is on a border cell, consider sector transition
        if (currentCell.getType() == GridCell.CellType.BORDER &&
                shouldTransitionSector(digimon, random)) {
            return handleSectorTransition(digimon, currentSector, currentCell, random);
        }

        // Otherwise, handle movement within current sector
        return handleIntraSectorMovement(digimon, currentSector, currentCell, random);
    }

    private static boolean shouldTransitionSector(Digimon digimon, Random random) {
        boolean isYoung = digimon.getAge() <= 25;
        boolean isHealthy = digimon.getHealth() >= 15;

        // Base chance modified by curiosity
        double transitionChance = 0.3 * (1 + digimon.getPersonality().getCuriosity());

        if (isYoung) transitionChance += 0.2;
        if (isHealthy) transitionChance += 0.1;
        if (digimon.shouldExplore()) transitionChance += 0.2;

        return random.nextDouble() < transitionChance;
    }

    private static boolean handleIntraSectorMovement(Digimon digimon, Sector sector,
                                                     GridCell currentCell, Random random) {
        Grid grid = sector.getGrid();
        int attempts = 0;

        while (attempts < MAX_PATH_ATTEMPTS) {
            // Generate target coordinates within movement radius
            int targetX = Math.max(1, Math.min(grid.getWidth() - 2,
                    currentCell.getX() + random.nextInt(MOVEMENT_RADIUS * 2 + 1) - MOVEMENT_RADIUS));
            int targetY = Math.max(1, Math.min(grid.getHeight() - 2,
                    currentCell.getY() + random.nextInt(MOVEMENT_RADIUS * 2 + 1) - MOVEMENT_RADIUS));

            GridCell targetCell = grid.getCell(targetX, targetY);

            // Skip if target is blocked or occupied
            if (isInvalidTarget(targetCell)) {
                attempts++;
                continue;
            }

            // Find path to target
            List<GridCell> path = sector.findPath(
                    currentCell.getX(), currentCell.getY(),
                    targetX, targetY
            );

            if (!path.isEmpty() && path.size() > 1) {
                // Move to next cell in path
                moveToCell(digimon, currentCell, path.get(1));
                return true;
            }

            attempts++;
        }

        // If no valid path found, try moving to any adjacent cell
        return attemptAdjacentMove(digimon, sector, currentCell, random);
    }

    private static boolean attemptAdjacentMove(Digimon digimon, Sector sector,
                                               GridCell currentCell, Random random) {
        Grid grid = sector.getGrid();
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        List<int[]> validMoves = new ArrayList<>();

        // Find all valid adjacent cells
        for (int[] dir : directions) {
            int newX = currentCell.getX() + dir[0];
            int newY = currentCell.getY() + dir[1];

            if (isValidPosition(newX, newY, grid)) {
                GridCell adjacentCell = grid.getCell(newX, newY);
                if (!isInvalidTarget(adjacentCell)) {
                    validMoves.add(dir);
                }
            }
        }

        // If valid moves exist, choose one randomly
        if (!validMoves.isEmpty()) {
            int[] chosenDir = validMoves.get(random.nextInt(validMoves.size()));
            GridCell targetCell = grid.getCell(
                    currentCell.getX() + chosenDir[0],
                    currentCell.getY() + chosenDir[1]
            );
            moveToCell(digimon, currentCell, targetCell);
            return true;
        }

        return false;
    }

    private static boolean handleSectorTransition(Digimon digimon, Sector currentSector,
                                                  GridCell currentCell, Random random) {
        // Get adjacent sectors
        List<Sector> adjacentSectors = currentSector.getAdjacentSectors();
        if (adjacentSectors.isEmpty()) {
            return false;
        }

        // Choose random adjacent sector
        Sector targetSector = adjacentSectors.get(random.nextInt(adjacentSectors.size()));

        // Find entry point in new sector
        GridCell entryCell = findEntryPoint(targetSector, random);
        if (entryCell == null) {
            return false;
        }

        // Perform transition
        transitionToNewSector(digimon, currentSector, targetSector, currentCell, entryCell);
        return true;
    }

    private static GridCell findEntryPoint(Sector sector, Random random) {
        Grid grid = sector.getGrid();
        List<GridCell> validEntryCells = new ArrayList<>();

        // Check all border cells
        for (int x = 0; x < grid.getWidth(); x++) {
            checkBorderCell(grid, x, 0, validEntryCells);
            checkBorderCell(grid, x, grid.getHeight() - 1, validEntryCells);
        }
        for (int y = 0; y < grid.getHeight(); y++) {
            checkBorderCell(grid, 0, y, validEntryCells);
            checkBorderCell(grid, grid.getWidth() - 1, y, validEntryCells);
        }

        return validEntryCells.isEmpty() ? null :
                validEntryCells.get(random.nextInt(validEntryCells.size()));
    }

    private static void checkBorderCell(Grid grid, int x, int y, List<GridCell> validCells) {
        GridCell cell = grid.getCell(x, y);
        if (cell.getType() == GridCell.CellType.BORDER && !isInvalidTarget(cell)) {
            validCells.add(cell);
        }
    }

    private static void transitionToNewSector(Digimon digimon, Sector currentSector,
                                              Sector targetSector, GridCell currentCell,
                                              GridCell entryCell) {
        currentSector.removeDigimon(digimon);
        currentCell.setOccupant(null);
        entryCell.setOccupant(digimon);
        targetSector.addDigimon(digimon);

        SimulationSubject.getInstance().notifyEvent(
                digimon.getName() + " has moved to sector " + targetSector.getName(),
                SimulationEvent.EventType.OTHER
        );
    }

    // Utility methods
    private static GridCell findDigimonCell(Digimon digimon, Sector sector) {
        Grid grid = sector.getGrid();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                GridCell cell = grid.getCell(x, y);
                if (digimon.equals(cell.getOccupant())) {
                    return cell;
                }
            }
        }
        return null;
    }

    private static boolean isValidPosition(int x, int y, Grid grid) {
        return x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight();
    }

    private static boolean isInvalidTarget(GridCell cell) {
        return cell.getType() == GridCell.CellType.BLOCKED ||
                cell.getOccupant() != null ||
                cell.getBuilding() != null;
    }

    private static void moveToCell(Digimon digimon, GridCell fromCell, GridCell toCell) {
        fromCell.setOccupant(null);
        toCell.setOccupant(digimon);
    }
}