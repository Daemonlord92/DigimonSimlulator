package com.horrorcore;

import java.util.List;
import java.util.Random;

public class BirthSystem {
    /**
     * Simulates the random birth of a new Digimon.
     * This method has a 10% chance of generating and adding a new Digimon to the provided list.
     *
     * @param digimonList The list of Digimon to which a new Digimon may be added.
     *                    This list is modified in place if a new Digimon is born.
     */
    public static void randomBirth(List<Digimon> digimonList) {
        Random random = new Random();
        if (random.nextInt(100) < 10) { // 10% chance of a new Digimon being born
            Digimon newDigimon = DigimonGenerator.generateRebirthDigimon();
            digimonList.add(newDigimon);
            VisualGUI.getInstance(null).addEvent("A new Digimon, " + newDigimon.getName() + ", has been born!", VisualGUI.EventType.OTHER);
        }
    }
}