package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Digimon {
    private String name;
    private int age;
    private int health;
    private int hunger;
    private int aggression;
    private Tribe tribe;
    private String stage;
    private int friendship;
    private String profession;
    private List<Digimon> friends;

    /**
     * Constructs a new Digimon with the specified attributes.
     */
    public Digimon(String name, int age, int health, int hunger, int aggression, String stage) {
        this.name = name;
        this.age = age;
        this.health = health;
        this.hunger = hunger;
        this.aggression = aggression;
        this.stage = stage;
        this.tribe = null;
        this.friendship = 0;
        this.profession = null;
        this.friends = new ArrayList<>();
    }

    /**
     * Constructs a new Digimon by copying the attributes of another Digimon.
     */
    public Digimon(Digimon other) {
        this(other.name, other.age, other.health, other.hunger, other.aggression, other.stage);
    }

    // Lifecycle methods

    public void ageUp() {
        this.age++;
        this.hunger += 10;
        if (this.age > 20) {
            this.health -= 5;
        }
    }

    public void eat() {
        int hungerReduction = switch (this.stage) {
            case "Rookie" -> 20;
            case "Champion" -> 30;
            case "Ultimate" -> 40;
            case "Mega" -> 50;
            default -> 20;
        };
        this.hunger = Math.max(0, this.hunger - hungerReduction);
    }

    public void attack(Digimon target) {
        if (this.aggression > 50) {
            int damage = switch (this.stage) {
                case "Rookie" -> 20;
                case "Champion" -> 30;
                case "Ultimate" -> 40;
                case "Mega" -> 50;
                default -> 10;
            };
            target.health -= damage;
            SimulationSubject.getInstance().notifyEvent(this.name + " attacked " + target.name + "!", SimulationEvent.EventType.ATTACK);
        }
    }

    public void joinTribe(String tribeName) {
        if (tribeName == null) {
            throw new IllegalArgumentException("Tribe name cannot be null.");
        }
        for (Tribe tribe : Tribe.getAllTribes()) {
            if (tribe.getName().equals(tribeName)) {
                this.tribe = tribe;
                tribe.getMembers().add(this);
                SimulationSubject.getInstance().notifyEvent(this.name + " joined the " + tribeName + " tribe.", SimulationEvent.EventType.POLITICAL);
                break;
            }
        }
    }

    public void evolve(String newName, String newStage) {
        SimulationSubject.getInstance().notifyEvent(this.name + " is evolving to " + newStage + " stage!", SimulationEvent.EventType.OTHER);

        this.name = newName;
        this.stage = newStage;

        switch (newStage) {
            case "In-Training" -> { this.health += 10; this.aggression += 5; }
            case "Rookie" -> { this.health += 20; this.aggression += 10; }
            case "Champion" -> { this.health += 40; this.aggression += 20; }
            case "Ultimate" -> { this.health += 60; this.aggression += 30; }
            case "Mega" -> { this.health += 80; this.aggression += 40; }
            default -> SimulationSubject.getInstance().notifyEvent("Unknown stage: " + newStage, SimulationEvent.EventType.OTHER);
        }

        SimulationSubject.getInstance().notifyEvent(this.name + " has evolved to " + this.stage + " stage!", SimulationEvent.EventType.OTHER);
    }


    public void increaseFriendship(Digimon tribeMember, int friendshipPoints) {
        this.friendship += friendshipPoints;
        this.friends.add(tribeMember);
    }

    public void decreaseFriendship(Digimon digimon, int friendship) {
        this.friendship -= friendship;
        this.friends.remove(digimon);
    }

    // Getters and Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = hunger; }

    public int getAggression() { return aggression; }
    public void setAggression(int aggression) { this.aggression = aggression; }

    public Tribe getTribe() { return tribe; }
    public void setTribe(Tribe tribe) { this.tribe = tribe; }

    public String getTribeName() { return tribe != null ? tribe.getName() : null; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public int getFriendship() { return friendship; }
    public void setFriendship(int friendship) { this.friendship = friendship; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public List<Digimon> getFriends() { return friends; }
    public void setFriends(List<Digimon> friends) { this.friends = friends; }

    // Utility methods

    public String getStatusString() {
        return "Name: " + this.name + 
               ", Age: " + this.age + 
               ", Health: " + this.health +
               ", Hunger: " + this.hunger + 
               ", Aggression: " + this.aggression + 
               ", Stage: " + this.stage +
               ", Profession: " + (this.profession != null ? this.profession : "None") +
               ", Tribe: " + (this.tribe != null ? this.tribe.getName() : "Independent");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Digimon digimon)) return false;
        return age == digimon.age &&
               health == digimon.health &&
               hunger == digimon.hunger &&
               aggression == digimon.aggression &&
               Objects.equals(name, digimon.name) &&
               Objects.equals(tribe, digimon.tribe) &&
               Objects.equals(stage, digimon.stage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, health, hunger, aggression, stage);
    }
}
