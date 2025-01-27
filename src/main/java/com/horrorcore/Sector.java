package com.horrorcore;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sector in the Digimon world.
 */
public class Sector {
    private final String name;
    private final List<Digimon> digimons;
    private List<Sector> adjacentSectors;

    /**
     * Constructs a new Sector with the given name.
     *
     * @param name The name of the sector.
     */
    public Sector(String name) {
        this.name = name;
        this.digimons = new ArrayList<>();
        this.adjacentSectors = new ArrayList<>();
    }

    /**
     * Adds a Digimon to this sector.
     *
     * @param digimon The Digimon to be added to the sector.
     */
    public void addDigimon(Digimon digimon) {
        if (digimon == null) {
            System.err.println("Warning: Attempted to add null Digimon to sector " + name);
            return;
        }
        digimons.add(digimon);
    }

    /**
     * Removes a Digimon from this sector.
     *
     * @param digimon The Digimon to be removed from the sector.
     */
    public void removeDigimon(Digimon digimon) {
        digimons.remove(digimon);
    }

    /**
     * Adds an adjacent sector to this sector's list of adjacent sectors.
     * If the sector is not already adjacent, it adds this sector to the other sector's adjacent list as well.
     *
     * @param sector The sector to be added as adjacent.
     */
    public void addAdjacentSector(Sector sector) {
        if (!adjacentSectors.contains(sector)) {
            adjacentSectors.add(sector);
            sector.addAdjacentSector(this);
        }
    }

    /**
     * Returns the list of Digimons in this sector.
     *
     * @return A List of Digimon objects present in this sector.
     */
    public List<Digimon> getDigimons() {
        return new ArrayList<>(digimons); // Return a copy to prevent external modifications
    }

    /**
     * Returns the list of adjacent sectors to this sector.
     *
     * @return A List of Sector objects that are adjacent to this sector.
     */
    public List<Sector> getAdjacentSectors() {
        return adjacentSectors;
    }

    /**
     * Returns the name of this sector.
     *
     * @return The name of the sector as a String.
     */
    public String getName() {
        return name;
    }
}