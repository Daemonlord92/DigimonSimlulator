package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SectorMovement {
    /**
     * Attempts to move a Digimon either within its current sector or to an adjacent sector.
     * @param digimon The Digimon to move
     * @param currentSector The sector where the Digimon currently is
     * @param random Random instance for movement decisions
     * @return true if movement was successful, false otherwise
     */
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

        // Otherwise, move within the current sector
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

    private static boolean handleSectorTransition(Digimon digimon, Sector currentSector,
                                                  GridCell currentCell, Random random) {
        Optional<Sector> targetSectorOptional = currentSector.getAdjacentSectors().stream().findAny();
        if (targetSectorOptional.isPresent()) {
            Sector targetSector = targetSectorOptional.get();

            // Remove from current sector
            currentSector.removeDigimon(digimon);
            currentCell.setOccupant(null);

            // Add to new sector at appropriate border
            try {
                // Find appropriate entry point in new sector
                GridCell entryCell = findEntryCell(targetSector, currentSector);
                if (entryCell != null) {
                    entryCell.setOccupant(digimon);
                    targetSector.addDigimon(digimon);
                    SimulationSubject.getInstance().notifyEvent(
                            digimon.getName() + " has moved to sector " + targetSector.getName(),
                            SimulationEvent.EventType.OTHER
                    );
                    return true;
                }
            } catch (IllegalStateException e) {
                // If addition to new sector fails, try to restore original position
                currentCell.setOccupant(digimon);
                currentSector.addDigimon(digimon);
            }
        }
        return false;
    }

    private static GridCell findEntryCell(Sector targetSector, Sector sourceSector) {
        // Determine which border to use based on sectors' relative positions
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

    private static boolean handleIntraSectorMovement(Digimon digimon, Sector sector,
                                                     GridCell currentCell, Random random) {
        int currentX = currentCell.getX();
        int currentY = currentCell.getY();

        // Generate random movement direction
        int dx = random.nextInt(3) - 1; // -1, 0, or 1
        int dy = random.nextInt(3) - 1; // -1, 0, or 1

        int newX = currentX + dx;
        int newY = currentY + dy;

        return sector.moveDigimon(digimon, newX, newY);
    }
}