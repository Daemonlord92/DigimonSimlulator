package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SectorMovement {
    private static final int MAX_PATH_ATTEMPTS = 5;

    public static boolean moveDigimon(Digimon digimon, Sector currentSector, Random random) {
        // Find the Digimon's current position
        GridCell currentCell = findDigimonCell(digimon, currentSector);
        if (currentCell == null) {
            return false;
        }

        // If Digimon is on a border cell, consider sector transition
        if (currentCell.getType() == GridCell.CellType.BORDER &&
                (digimon.getAge() <= 25 || digimon.getHealth() >= 15 && random.nextBoolean())) {
            return handleSectorTransition(digimon, currentSector, currentCell, random);
        }

        // Otherwise, move within the current sector using pathfinding
        return handleIntraSectorMovement(digimon, currentSector, currentCell, random);
    }

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

    private static boolean handleIntraSectorMovement(Digimon digimon, Sector sector,
                                                     GridCell currentCell, Random random) {
        Grid grid = sector.getGrid();
        int attempts = 0;

        while (attempts < MAX_PATH_ATTEMPTS) {
            // Generate random target coordinates
            int targetX = 1 + random.nextInt(grid.getWidth() - 2);
            int targetY = 1 + random.nextInt(grid.getHeight() - 2);
            GridCell targetCell = grid.getCell(targetX, targetY);

            // Skip if target cell is blocked or occupied
            if (targetCell.getType() == GridCell.CellType.BLOCKED ||
                    targetCell.getOccupant() != null ||
                    targetCell.getBuilding() != null) {
                attempts++;
                continue;
            }

            // Try to find a path to the target
            List<GridCell> path = sector.findPath(currentCell.getX(), currentCell.getY(),
                    targetX, targetY);

            if (!path.isEmpty()) {
                // Move along the first step of the path
                GridCell nextCell = path.get(1); // Index 0 is current cell
                currentCell.setOccupant(null);
                nextCell.setOccupant(digimon);
                return true;
            }

            attempts++;
        }

        return false;
    }

    private static boolean handleSectorTransition(Digimon digimon, Sector currentSector,
                                                  GridCell currentCell, Random random) {
        Optional<Sector> targetSectorOptional = currentSector.getAdjacentSectors().stream()
                .findAny();

        if (targetSectorOptional.isPresent()) {
            Sector targetSector = targetSectorOptional.get();
            GridCell entryCell = findEntryCell(targetSector);

            if (entryCell != null) {
                // Remove from current sector
                currentSector.removeDigimon(digimon);
                currentCell.setOccupant(null);

                // Add to new sector
                entryCell.setOccupant(digimon);
                targetSector.addDigimon(digimon);

                SimulationSubject.getInstance().notifyEvent(
                        digimon.getName() + " has moved to sector " + targetSector.getName(),
                        SimulationEvent.EventType.OTHER
                );
                return true;
            }
        }
        return false;
    }

    private static GridCell findEntryCell(Sector targetSector) {
        Grid grid = targetSector.getGrid();
        List<GridCell> availableBorderCells = new ArrayList<>();

        // Check all border cells for an empty spot
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                GridCell cell = grid.getCell(x, y);
                if (cell.getType() == GridCell.CellType.BORDER &&
                        cell.getOccupant() == null &&
                        cell.getBuilding() == null) {
                    availableBorderCells.add(cell);
                }
            }
        }

        if (!availableBorderCells.isEmpty()) {
            return availableBorderCells.get(new Random().nextInt(availableBorderCells.size()));
        }
        return null;
    }
}