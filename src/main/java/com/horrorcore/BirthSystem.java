package com.horrorcore;

import java.util.List;
import java.util.Random;

public class BirthSystem {
    /**
     * Simulates the random birth of a new Digimon.
     * This method has a 25% chance of generating and adding a new Digimon to the provided list.
     *
     * @param digimonList The list of Digimon to which a new Digimon may be added.
     *                    This list is modified in place if a new Digimon is born.
     */
    public static void randomBirth(List<Digimon> digimonList) {
        Random random = new Random();
        if (random.nextInt(100) < 25) { // 25% chance
            Digimon newDigimon = DigimonGenerator.generateRebirthDigimon();
            // Add to the provided sector's list instead
            digimonList.add(newDigimon);
            SimulationSubject.getInstance().notifyEvent(
                    "A new Digimon, " + newDigimon.getName() + ", has been born!",
                    SimulationEvent.EventType.OTHER
            );
        }
    }
}