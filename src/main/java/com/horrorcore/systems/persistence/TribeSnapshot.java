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
    
    public void restoreToWorld(World world) {
        // Tribes will be reconstructed as Digimon rejoin them
        // This is a simplified implementation
        // Full implementation requires finding matching Digimon by name
    }
    
    // Getters
    public String getName() { return name; }
    public List<String> getMemberNames() { return memberNames; }
    public String getLeaderName() { return leaderName; }
    public int getBuildings() { return buildings; }
    public int getTotalFood() { return totalFood; }
    public int getMilitaryStrength() { return militaryStrength; }
}
