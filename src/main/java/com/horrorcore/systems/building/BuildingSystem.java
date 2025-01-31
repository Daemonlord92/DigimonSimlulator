package com.horrorcore.systems.building;

import com.horrorcore.World;
import com.horrorcore.entity.Building;
import com.horrorcore.entity.Sector;
import com.horrorcore.entity.Tribe;
import com.horrorcore.grid.Grid;
import com.horrorcore.grid.GridCell;

public class BuildingSystem {
    /**
     * Attempts to build a city for a tribe in their current sector.
     * This method will try to place a city center and surrounding buildings
     * in a valid configuration within the sector's grid.
     *
     * @param tribe The tribe building the city
     * @param world The current world instance
     * @return boolean indicating if city was successfully built
     */
    public static boolean buildCity(Tribe tribe, World world) {
        // Find the sector where the tribe's leader is
        Sector tribeSector = world.getSectors().stream()
                .filter(sector -> sector.getDigimons().contains(tribe.getLeader()))
                .findFirst()
                .orElse(null);

        if (tribeSector == null) {
            return false;
        }

        Grid grid = tribeSector.getGrid();

        // Find a suitable location for the city center
        BuildingLocation cityCenter = findBuildingLocation(grid, Building.BuildingType.CITY_CENTER);
        if (cityCenter == null) {
            return false;
        }

        // Place the city center
        Building cityCenterBuilding = new Building(Building.BuildingType.CITY_CENTER, tribe,
                cityCenter.x(), cityCenter.y());
        if (!tribeSector.placeBuilding(cityCenterBuilding, cityCenter.x(), cityCenter.y())) {
            return false;
        }

        // Try to place surrounding buildings
        placeAdjacentBuildings(tribeSector, tribe, cityCenter);

        return true;
    }

    private static void placeAdjacentBuildings(Sector sector, Tribe tribe, BuildingLocation cityCenter) {
        // Attempt to place different building types around the city center
        Building.BuildingType[] buildingTypes = {
                Building.BuildingType.HOUSE,
                Building.BuildingType.FARM,
                Building.BuildingType.BARRACKS
        };

        int[][] offsets = {
                {-2, -2}, {-2, 2}, {2, -2}, {2, 2},  // Corner buildings
                {0, -3}, {0, 3}, {-3, 0}, {3, 0}     // Cardinal direction buildings
        };

        for (int i = 0; i < offsets.length && i < buildingTypes.length; i++) {
            int newX = cityCenter.x() + offsets[i][0];
            int newY = cityCenter.y() + offsets[i][1];

            if (isValidBuildingLocation(sector.getGrid(), newX, newY, buildingTypes[i % buildingTypes.length])) {
                Building building = new Building(buildingTypes[i % buildingTypes.length],
                        tribe, newX, newY);
                sector.placeBuilding(building, newX, newY);
            }
        }
    }

    private static BuildingLocation findBuildingLocation(Grid grid, Building.BuildingType type) {
        int radius = type.getRadius();

        // Search for a suitable location
        for (int x = radius; x < grid.getWidth() - radius; x++) {
            for (int y = radius; y < grid.getHeight() - radius; y++) {
                if (isValidBuildingLocation(grid, x, y, type)) {
                    return new BuildingLocation(x, y);
                }
            }
        }
        return null;
    }

    private static boolean isValidBuildingLocation(Grid grid, int x, int y, Building.BuildingType type) {
        int radius = type.getRadius();

        // Check if location is within grid bounds
        if (x < radius || x >= grid.getWidth() - radius ||
                y < radius || y >= grid.getHeight() - radius) {
            return false;
        }

        // Check the building area and surrounding cells
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int checkX = x + dx;
                int checkY = y + dy;

                GridCell cell = grid.getCell(checkX, checkY);
                if (cell == null ||
                        cell.getType() == GridCell.CellType.BLOCKED ||
                        cell.getType() == GridCell.CellType.BORDER ||
                        cell.getBuilding() != null ||
                        cell.getOccupant() != null) {
                    return false;
                }
            }
        }

        return true;
    }
}
