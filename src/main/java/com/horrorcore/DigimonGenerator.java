package com.horrorcore;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Random;

public class DigimonGenerator {
    private static final String[] NAMES = {"Agumon", "Gabumon", "Patamon", "Biyomon", "Tentomon", "Palmon", "Gomamon"};
    private static final String[] STAGES = {"Fresh","In-Training", "Rookie", "Champion", "Ultimate", "Mega"};
    private static final List<Digimon> digimonList;

    /**
     * Static initializer block for loading Digimon data from a JSON file.
     * This block is executed when the class is loaded and initializes the digimonList.
     * 
     * The method performs the following steps:
     * 1. Creates a new Gson instance for JSON parsing.
     * 2. Loads the "digimon.json" file from the classpath as an InputStream.
     * 3. Creates an InputStreamReader from the InputStream.
     * 4. Defines the Type for a List of Digimon objects.
     * 5. Parses the JSON data into a List of Digimon objects.
     * 
     * If any JSON-related exceptions occur during this process, they are caught
     * and wrapped in a RuntimeException.
     * 
     * @throws RuntimeException if there's an error in JSON syntax or I/O operations
     */
    static {
        try {
            Gson gson = new Gson();
            InputStream is = DigimonGenerator.class.getClassLoader().getResourceAsStream("digimon.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<Digimon>>(){}.getType();
            digimonList = gson.fromJson(reader, listType);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random Digimon with modified attributes.
     * This method selects a random Digimon from the available list and creates
     * a new instance with slightly adjusted health.
     *
     * @return A new Digimon instance with randomly selected attributes and modified health.
     *         The health value is increased by 40 points from the original Digimon's health.
     */
    public static Digimon generateRandomDigimon() {
        Random random = new Random();
        Digimon randomDigimon = digimonList.get(random.nextInt(digimonList.size()));
        String name = randomDigimon.getName();
        int age = randomDigimon.getAge();
        int health = randomDigimon.getHealth() + 40; // Health between 80 and 120
        int hunger = randomDigimon.getHunger();
        int aggression = randomDigimon.getAggression();
        String stage = randomDigimon.getStage();
        return new Digimon(name, age, health, hunger, aggression, stage);
    }

    public static List<Digimon> getAllDigimon() {
        return digimonList;
    }
/**
 * Generates a reborn Digimon with adjusted stats.
 * This method selects a random In-Training or Rookie Digimon from the available list
 * and creates a new instance with modified attributes to represent a rebirth.
 *
 * @return A new Digimon instance representing a reborn Digimon with adjusted stats.
 * @throws RuntimeException if no In-Training or Rookie Digimon are found in the list.
 */
public static Digimon generateRebirthDigimon() {
    Random random = new Random();
    List<Digimon> rebirthCandidates = digimonList.stream()
            .filter(d -> d.getStage().equals("In-Training") || d.getStage().equals("Rookie") || d.getStage().equals("Fresh"))
            .collect(Collectors.toList());

    if (rebirthCandidates.isEmpty()) {
        throw new RuntimeException("No In-Training or Rookie Digimon found for rebirth.");
    }

    Digimon rebirthDigimon = rebirthCandidates.get(random.nextInt(rebirthCandidates.size()));
    
    // Adjust stats for rebirth
    int health = rebirthDigimon.getHealth();
    int hunger = random.nextInt(21) + 40; // Hunger between 40 and 60
    int aggression = random.nextInt(16) + 5; // Aggression between 5 and 20

    return new Digimon(rebirthDigimon.getName(), 0, health, hunger, aggression, rebirthDigimon.getStage());
}
}