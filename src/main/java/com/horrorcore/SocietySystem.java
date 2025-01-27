package com.horrorcore;

import java.util.List;

public class SocietySystem {
    public static void formTribes(List<Digimon> digimonList) {
        Tribe fireTribe = new Tribe("Fire");
        Tribe waterTribe = new Tribe("Water");
        for (Digimon digimon : digimonList) {
            if (digimon.getAggression() > 50) {
                fireTribe.addMember(digimon);
            } else {
                waterTribe.addMember(digimon);
            }
        }
    }
}
