package com.horrorcore.systems.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.horrorcore.entity.Digimon;

public class DigimonSnapshot {
    private final String name;
    private final int age;
    private final int health;
    private final int hunger;
    private final int aggression;
    private final String stage;
    private final String profession;
    private final String tribeName;
    
    @JsonCreator
    public DigimonSnapshot(
            @JsonProperty("name") String name,
            @JsonProperty("age") int age,
            @JsonProperty("health") int health,
            @JsonProperty("hunger") int hunger,
            @JsonProperty("aggression") int aggression,
            @JsonProperty("stage") String stage,
            @JsonProperty("profession") String profession,
            @JsonProperty("tribeName") String tribeName) {
        this.name = name;
        this.age = age;
        this.health = health;
        this.hunger = hunger;
        this.aggression = aggression;
        this.stage = stage;
        this.profession = profession;
        this.tribeName = tribeName;
    }
    
    public static DigimonSnapshot fromDigimon(Digimon digimon) {
        return new DigimonSnapshot(
                digimon.getName(),
                digimon.getAge(),
                digimon.getHealth(),
                digimon.getHunger(),
                digimon.getAggression(),
                digimon.getStage(),
                digimon.getProfession(),
                digimon.getTribeName()
        );
    }
    
    public Digimon toDigimon() {
        Digimon digimon = new Digimon(name, age, health, hunger, aggression, stage);
        digimon.setProfession(profession);
        // Note: Tribe membership restored separately
        return digimon;
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public int getHealth() { return health; }
    public int getHunger() { return hunger; }
    public int getAggression() { return aggression; }
    public String getStage() { return stage; }
    public String getProfession() { return profession; }
    public String getTribeName() { return tribeName; }
}
