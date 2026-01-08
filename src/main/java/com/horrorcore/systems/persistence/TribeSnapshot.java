package com.horrorcore.systems.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.horrorcore.World;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Tribe;

import java.util.List;
import java.util.stream.Collectors;

public class TribeSnapshot {
    private final String name;
    private final List<String> memberNames;
    private final String leaderName;
    private final int buildings;
    private final int totalFood;
    private final int militaryStrength;
    
    @JsonCreator
    public TribeSnapshot(
            @JsonProperty("name") String name,
            @JsonProperty("memberNames") List<String> memberNames,
            @JsonProperty("leaderName") String leaderName,
            @JsonProperty("buildings") int buildings,
            @JsonProperty("totalFood") int totalFood,
            @JsonProperty("militaryStrength") int militaryStrength) {
        this.name = name;
        this.memberNames = memberNames;
        this.leaderName = leaderName;
        this.buildings = buildings;
        this.totalFood = totalFood;
        this.militaryStrength = militaryStrength;
    }
    
    public static TribeSnapshot fromTribe(Tribe tribe) {
        List<String> memberNames = tribe.getMembers().stream()
                .map(Digimon::getName)
                .collect(Collectors.toList());
                
        return new TribeSnapshot(
                tribe.getName(),
                memberNames,
                tribe.getLeader() != null ? tribe.getLeader().getName() : null,
                tribe.getBuildings(),
                tribe.getTotalFood(),
                tribe.getMilitaryStrength()
        );
    }
    
    /**
     * Restores tribe data to the world.
     * Note: This is a simplified implementation as documented in the requirements.
     * Full tribe restoration is complex and deferred to future enhancements.
     * 
     * Complete implementation would require:
     * 1. Finding matching Digimon by name across all sectors
     * 2. Creating new Tribe instances with preserved IDs
     * 3. Reassigning Digimon to tribes while preserving relationships
     * 4. Restoring tribe resources (food, buildings, military strength)
     * 5. Restoring technology levels and profession assignments
     * 
     * Current behavior: Tribe data is saved but not fully restored.
     * Tribes will naturally reform during simulation.
     */
    public void restoreToWorld(World world) {
        // Intentionally simplified per requirements
        // See method documentation for details on future enhancements
    }
    
    // Getters
    public String getName() { return name; }
    public List<String> getMemberNames() { return memberNames; }
    public String getLeaderName() { return leaderName; }
    public int getBuildings() { return buildings; }
    public int getTotalFood() { return totalFood; }
    public int getMilitaryStrength() { return militaryStrength; }
}
