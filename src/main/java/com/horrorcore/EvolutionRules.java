package com.horrorcore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class EvolutionRules {
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(EvolutionRules.class));
    private static Map<String, Map<String, String>> EVOLUTION_RULES;

    static {
        try {
            Gson gson = new Gson();
            InputStream inputStream = EvolutionRules.class.getClassLoader()
                    .getResourceAsStream("evolution_rules.json");
            assert inputStream != null;
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
            EVOLUTION_RULES = gson.fromJson(reader, type);
        } catch (Exception e) {
            LOGGER.fine(e.getMessage());
            EVOLUTION_RULES = new HashMap<>(); // Fallback to empty map
        }
    }

    /**
     * Determines the next evolution for a given Digimon based on its current name and stage.
     *
     * @param currentName  The current name of the Digimon.
     * @param currentStage The current evolution stage of the Digimon.
     * @return The name of the next evolution if available, or null if no evolution is found.
     */
    public static String getNextEvolution(String currentName, String currentStage) {
        if (EVOLUTION_RULES.containsKey(currentName)) {
            Map<String, String> path = EVOLUTION_RULES.get(currentName);
            if (path.containsKey(currentStage)) {
                return path.get(currentStage);
            }
        }
        return null; // No evolution available
    }
}