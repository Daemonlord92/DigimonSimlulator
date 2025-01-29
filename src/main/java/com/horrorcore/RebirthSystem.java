package com.horrorcore;

import java.util.List;

public class RebirthSystem {
    /**
     * Checks and processes the rebirth of Digimon in the given list.
     * This method iterates through the list of Digimon, identifies those with zero or negative health,
     * and replaces them with reborn Digimon. The rebirth process depends on the Digimon's current stage.
     *
     * @param digimonList A List of Digimon objects to be checked for rebirth.
     *                    This list is modified in place, with dead Digimon replaced by reborn ones.
     */
    public static void checkRebirth(List<Digimon> digimonList) {
        for (int i = 0; i < digimonList.size(); i++) {
            Digimon digimon = digimonList.get(i);
            if (digimon.getHealth() <= 0) {
                SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has died and will be reborn!", SimulationEvent.EventType.OTHER);
                Digimon rebornDigimon;
                if (!digimon.getStage().equals("Rookie") || !digimon.getStage().equals("In-Training")) {
                    rebornDigimon = DigimonGenerator.generateRebirthDigimon();
                } else {
                    rebornDigimon = DigimonGenerator.getAllDigimon()
                            .stream()
                            .filter(d -> d.getName()
                                    .equals(digimon.getName()))
                            .distinct()
                            .findFirst()
                            .orElse(null);
                }

                digimonList.set(i, rebornDigimon);
                SimulationSubject.getInstance().notifyEvent(rebornDigimon.getName() + " has been reborn as a Baby!", SimulationEvent.EventType.OTHER);
            }
        }
    }
}