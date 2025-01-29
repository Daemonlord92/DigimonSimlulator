package com.horrorcore;

import java.util.List;
import java.util.Random;

public class CelestialDigimon extends Digimon {
    private static int totalCelestials = 0;
    private static final int MAX_CELESTIALS = 10;

    public CelestialDigimon(String name, int age, int health, int hunger, int aggression, String stage) {
        super(name, age, health, hunger, aggression, stage);
        if (totalCelestials >= MAX_CELESTIALS) {
            throw new IllegalStateException("Maximum number of Celestial Digimon reached");
        }
        totalCelestials++;
    }

    @Override
    public void joinTribe(String tribeName) {
        // Celestials don't join tribes
    }

    @Override
    public void attack(Digimon target) {
        if (isTargetMassKiller(target)) {
            super.attack(target);
            SimulationSubject.getInstance().notifyEvent(
                    getName() + " has judged " + target.getName() + " for their crimes!",
                    SimulationEvent.EventType.ATTACK
            );
        }
    }

    private boolean isTargetMassKiller(Digimon target) {
        return target.getAggression() > 750;
    }

    public void provideFood(List<Digimon> digimons) {
        for (Digimon digimon : digimons) {
            digimon.setHunger(Math.max(0, digimon.getHunger() - 30));
        }
        SimulationSubject.getInstance().notifyEvent(
                getName() + " has provided food to nearby Digimon",
                SimulationEvent.EventType.OTHER
        );
    }

    public void heal(List<Digimon> digimons) {
        for (Digimon digimon : digimons) {
            digimon.setHealth(Math.min(100, digimon.getHealth() + 20));
        }
        SimulationSubject.getInstance().notifyEvent(
                getName() + " has healed nearby Digimon",
                SimulationEvent.EventType.OTHER
        );
    }

    @Override
    public void ageUp() {
        // Celestials don't age
    }

    public static int getTotalCelestials() {
        return totalCelestials;
    }
}
