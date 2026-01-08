package com.horrorcore.systems.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SectorSnapshot {
    private final String name;
    private final List<DigimonSnapshot> digimons;
    
    @JsonCreator
    public SectorSnapshot(
            @JsonProperty("name") String name,
            @JsonProperty("digimons") List<DigimonSnapshot> digimons) {
        this.name = name;
        this.digimons = digimons;
    }
    
    public static SectorSnapshot fromSector(Sector sector) {
        List<DigimonSnapshot> digimonSnapshots = sector.getDigimons().stream()
                .map(DigimonSnapshot::fromDigimon)
                .collect(Collectors.toList());
                
        return new SectorSnapshot(sector.getName(), digimonSnapshots);
    }
    
    public void restoreToSector(Sector sector) {
        // Clear existing Digimon
        List<Digimon> existing = new ArrayList<>(sector.getDigimons());
        existing.forEach(sector::removeDigimon);
        
        // Add restored Digimon
        for (DigimonSnapshot snapshot : digimons) {
            Digimon digimon = snapshot.toDigimon();
            try {
                sector.addDigimon(digimon);
            } catch (IllegalStateException e) {
                // Sector full, skip this Digimon
            }
        }
    }
    
    public String getName() { return name; }
    public List<DigimonSnapshot> getDigimons() { return digimons; }
}
