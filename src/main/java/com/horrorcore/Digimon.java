package com.horrorcore;

import java.util.ArrayList;
import java.util.List;

public class Digimon {
    private String name;
    private int age;
    private int health;
    private int hunger;
    private int aggression;
    private Tribe tribe;
    private String stage;

    /**
     * Constructs a new Digimon with the specified attributes.
     *
     * @param name       The name of the Digimon.
     * @param age        The age of the Digimon.
     * @param health     The health points of the Digimon.
     * @param hunger     The hunger level of the Digimon.
     * @param aggression The aggression level of the Digimon.
     * @param stage      The current evolutionary stage of the Digimon.
     */
    public Digimon(String name, int age, int health, int hunger, int aggression, String stage) {
        this.name = name;
        this.age = age;
        this.health = health;
        this.hunger = hunger;
        this.aggression = aggression;
        this.stage = stage;
        this.tribe = null; // Initially, no tribe
    }

    /**
     * Constructs a new Digimon by copying the attributes of another Digimon.
     *
     * @param other The Digimon to copy attributes from.
     */
    public Digimon(Digimon other) {
        this.name = other.name;
        this.age = other.age;
        this.health = other.health;
        this.hunger = other.hunger;
        this.aggression = other.aggression;
        this.stage = other.stage;
        this.tribe = null;
    }

    /**
     * Increases the age of the Digimon and adjusts its attributes accordingly.
     * Increases hunger and potentially decreases health for older Digimon.
     */
    public void ageUp() {
        this.age++;
        this.hunger += 10; // Increase hunger with age
        if (this.age > 20) {
            this.health -= 5; // Health declines with old age
        }
    }

    /**
     * Reduces the hunger level of the Digimon.
     */
    public void eat() {
        this.hunger -= 20;
        if (this.hunger < 0) this.hunger = 0;
    }

    /**
     * Attempts to attack another Digimon if this Digimon's aggression is high enough.
     *
     * @param target The Digimon to attack.
     */
    public void attack(Digimon target) {
        if (this.aggression > 50) {
            target.health -= 10;
            VisualGUI.getInstance(null).addEvent(this.name + " attacked " + target.name + "!", VisualGUI.EventType.ATTACK);
        }
    }

    /**
     * Assigns the Digimon to a tribe.
     *
     * @param tribeName The name of the tribe to join.
     * @throws IllegalArgumentException if the tribe name is null.
     */
    public void joinTribe(String tribeName) {
        if (tribeName != null) {
            this.tribe = new Tribe(tribeName);
            VisualGUI.getInstance(null).addEvent(this.name + " joined the " + tribeName + " tribe.", VisualGUI.EventType.POLITICAL);
        } else {
            throw new IllegalArgumentException("Tribe name cannot be null.");
        }
    }

    /**
     * Evolves the Digimon to a new stage, updating its name and attributes.
     *
     * @param newName  The new name of the Digimon after evolution.
     * @param newStage The new evolutionary stage of the Digimon.
     */
    public void evolve(String newName, String newStage) {
        VisualGUI.getInstance(null).addEvent(this.name + " is evolving to " + newStage + " stage!", VisualGUI.EventType.OTHER);

        // Update name and stage
        this.name = newName;
        this.stage = newStage;

        // Adjust attributes based on the new stage
        switch (newStage) {
            case "Rookie":
                this.health += 20;
                this.aggression += 10;
                break;
            case "Champion":
                this.health += 40;
                this.aggression += 20;
                break;
            case "Ultimate":
                this.health += 60;
                this.aggression += 30;
                break;
            case "Mega":
                this.health += 80;
                this.aggression += 40;
                break;
            default:
                VisualGUI.getInstance(null).addEvent("Unknown stage: " + newStage, VisualGUI.EventType.OTHER);
                break;
        }

        VisualGUI.getInstance(null).addEvent(this.name + " has evolved to " + this.stage + " stage!", VisualGUI.EventType.OTHER);
    }

    /**
     * Gets the name of the Digimon.
     *
     * @return The name of the Digimon.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Digimon.
     *
     * @param name The new name for the Digimon.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the age of the Digimon.
     *
     * @return The age of the Digimon.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the Digimon.
     *
     * @param age The new age for the Digimon.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets the health of the Digimon.
     *
     * @return The health points of the Digimon.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the Digimon.
     *
     * @param health The new health points for the Digimon.
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Gets the hunger level of the Digimon.
     *
     * @return The hunger level of the Digimon.
     */
    public int getHunger() {
        return hunger;
    }

    /**
     * Sets the hunger level of the Digimon.
     *
     * @param hunger The new hunger level for the Digimon.
     */
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    /**
     * Gets the aggression level of the Digimon.
     *
     * @return The aggression level of the Digimon.
     */
    public int getAggression() {
        return aggression;
    }

    /**
     * Sets the aggression level of the Digimon.
     *
     * @param aggression The new aggression level for the Digimon.
     */
    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    /**
     * Gets the tribe of the Digimon.
     *
     * @return The Tribe object representing the Digimon's tribe.
     */
    public Tribe getTribe() {
        return tribe;
    }

    /**
     * Gets the name of the Digimon's tribe.
     *
     * @return The name of the Digimon's tribe.
     */
    public String getTribeName() {
        return tribe.getName();
    }

    /**
     * Sets the tribe of the Digimon.
     *
     * @param tribeName The name of the new tribe for the Digimon.
     * @throws IllegalArgumentException if the tribe name is null.
     */
    public void setTribe(String tribeName) {
        if (tribeName != null) {
            this.tribe = new Tribe(tribeName);
            VisualGUI.getInstance(null).addEvent(this.name + " joined the " + tribeName + " tribe.", VisualGUI.EventType.POLITICAL);
        } else {
            throw new IllegalArgumentException("Tribe name cannot be null.");
        }
    }

    /**
     * Gets the current evolutionary stage of the Digimon.
     *
     * @return The current stage of the Digimon.
     */
    public String getStage() {
        return stage;
    }

    /**
     * Sets the evolutionary stage of the Digimon.
     *
     * @param stage The new stage for the Digimon.
     */
    public void setStage(String stage) {
        this.stage = stage;
    }
    /**
     * Returns a string representation of the Digimon's current status.
     * 
     * This method provides a concise summary of the Digimon's attributes,
     * including its name, age, health, hunger level, aggression level, and tribe affiliation.
     * 
     * @return A string containing the Digimon's status information, formatted as:
     *         "Name: [name], Age: [age], Health: [health], Hunger: [hunger], 
     *         Aggression: [aggression], Tribe: [tribeName or 'independent']"
     */
    public String getStatusString() {
        return "Name: " + this.name + ", Age: " + this.age + ", Health: " + this.health +
                ", Hunger: " + this.hunger + ", Aggression: " + this.aggression + ", Tribe: "
                + (this.tribe != null ? this.tribe.getName():"independent");
    }
}
