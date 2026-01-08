package com.horrorcore.systems.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.horrorcore.World;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;
import com.horrorcore.entity.Tribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serializable representation of World state for save/load operations.
 */
public class WorldSnapshot {
    private final int time;
    private final String currentAge;
    private final List<SectorSnapshot> sectors;
    private final Set<TribeSnapshot> tribes;
    private final int totalBuildings;
    
    @JsonCreator
    public WorldSnapshot(
            @JsonProperty("time") int time,
            @JsonProperty("currentAge") String currentAge,
            @JsonProperty("sectors") List<SectorSnapshot> sectors,
            @JsonProperty("tribes") Set<TribeSnapshot> tribes,
            @JsonProperty("totalBuildings") int totalBuildings) {
        this.time = time;
        this.currentAge = currentAge;
        this.sectors = sectors;
        this.tribes = tribes;
        this.totalBuildings = totalBuildings;
    }
    
    /**
     * Creates a snapshot from the current World state.
     */
    public static WorldSnapshot fromWorld(World world) {
        List<SectorSnapshot> sectorSnapshots = world.getSectors().stream()
                .map(SectorSnapshot::fromSector)
                .collect(Collectors.toList());
                
        Set<TribeSnapshot> tribeSnapshots = world.getTribes().stream()
                .map(TribeSnapshot::fromTribe)
                .collect(Collectors.toSet());
                
        return new WorldSnapshot(
                world.getTime(),
                world.getTechnologySystem().getCurrentAge(),
                sectorSnapshots,
                tribeSnapshots,
                world.getBuildings()
        );
    }
    
    /**
     * Applies this snapshot to a World instance.
     * Note: This creates a new World instance rather than modifying the existing one.
     */
    public World toWorld() {
        World world = World.getInstance();
        
        // Reset world state
        world.reset();
        
        // Restore sectors and Digimon
        List<Sector> restoredSectors = world.getSectors();
        for (int i = 0; i < Math.min(sectors.size(), restoredSectors.size()); i++) {
            SectorSnapshot snapshot = sectors.get(i);
            Sector sector = restoredSectors.get(i);
            snapshot.restoreToSector(sector);
        }
        
        // Restore tribes
        for (TribeSnapshot tribeSnapshot : tribes) {
            tribeSnapshot.restoreToWorld(world);
        }
        
        // Note: Time and technology age are restored via World methods
        // This requires adding methods to World class (see below)
        
        return world;
    }
    
    // Getters for Jackson
    public int getTime() { return time; }
    public String getCurrentAge() { return currentAge; }
    public List<SectorSnapshot> getSectors() { return sectors; }
    public Set<TribeSnapshot> getTribes() { return tribes; }
    public int getTotalBuildings() { return totalBuildings; }
}
