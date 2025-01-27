package com.horrorcore;

public class EvolutionSystem {
    /**
     * Checks if a Digimon is eligible for evolution and evolves it if conditions are met.
     * This method examines the Digimon's current age and stage, and determines if it's
     * ready to evolve based on predefined age requirements for each stage. If eligible,
     * it retrieves the next evolution form and applies it to the Digimon.
     *
     * @param digimon The Digimon object to check for evolution eligibility.
     */
    public static void checkEvolution(Digimon digimon) {
        String currentName = digimon.getName();
        String currentStage = digimon.getStage();
        int age = digimon.getAge();

        // Check if the Digimon is old enough to evolve
        if (age >= getEvolutionAgeRequirement(currentStage)) {
            String nextEvolution = EvolutionRules.getNextEvolution(currentName, currentStage);
            if (nextEvolution != null) {
                String[] parts = nextEvolution.split(":");
                String newName = parts[0];
                String newStage = parts[1];
                digimon.evolve(newName, newStage);
            }
        }
    }

    private static int getEvolutionAgeRequirement(String stage) {
        switch (stage) {
            case "In-Training": return 5;
            case "Rookie": return 10;
            case "Champion": return 15;
            case "Ultimate": return 20;
            default: return Integer.MAX_VALUE; // No evolution
        }
    }
}
