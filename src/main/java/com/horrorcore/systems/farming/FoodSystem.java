package com.horrorcore.systems.farming;

import com.horrorcore.entity.Digimon;

import java.util.List;

public class FoodSystem {
    public static void distributeFood(List<Digimon> digimonList) {
        for (Digimon digimon : digimonList) {
            if (digimon.getHunger() > 50) {
                digimon.eat();
            }
        }
    }
}
