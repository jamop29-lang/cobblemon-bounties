package com.cobblebounties.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cobblebounties.CobbleBounties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BountyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/cobblebounties.json");
    
    // Configuration values
    public int activeBountyCount = 3;
    public int bountyDurationHours = 24;
    public int minCaptureCount = 1;
    public int maxCaptureCount = 5;
    public int baseMoneyReward = 500;
    public boolean broadcastNewBounties = true;
    public boolean broadcastCompletion = true;
    
    // Reward pools
    public List<String> commonRewards = Arrays.asList(
        "cobblemon:poke_ball:3",
        "cobblemon:potion:2"
    );
    
    public List<String> uncommonRewards = Arrays.asList(
        "cobblemon:great_ball:2",
        "cobblemon:super_potion:2",
        "cobblemon:rare_candy:1"
    );
    
    public List<String> rareRewards = Arrays.asList(
        "cobblemon:ultra_ball:2",
        "cobblemon:hyper_potion:2",
        "cobblemon:fire_stone:1",
        "cobblemon:water_stone:1",
        "cobblemon:thunder_stone:1"
    );
    
    public List<String> legendaryRewards = Arrays.asList(
        "cobblemon:master_ball:1",
        "cobblemon:rare_candy:3",
        "cobblemon:ability_capsule:1"
    );
    
    // Pokemon rarity definitions
    public List<String> commonPokemon = Arrays.asList(
        "pidgey", "rattata", "caterpie", "weedle", "magikarp", 
        "bidoof", "starly", "zigzagoon", "wurmple", "poochyena"
    );
    
    public List<String> uncommonPokemon = Arrays.asList(
        "pikachu", "eevee", "growlithe", "vulpix", "psyduck",
        "machop", "geodude", "gastly", "abra", "dratini"
    );
    
    public List<String> rarePokemon = Arrays.asList(
        "charizard", "blastoise", "venusaur", "dragonite", "tyranitar",
        "salamence", "metagross", "garchomp", "lucario", "zoroark"
    );
    
    public List<String> legendaryPokemon = Arrays.asList(
        "articuno", "zapdos", "moltres", "mewtwo", "lugia", "ho-oh",
        "kyogre", "groudon", "rayquaza", "dialga", "palkia", "giratina"
    );
    
    public static BountyConfig load() {
        if (!CONFIG_FILE.exists()) {
            BountyConfig config = new BountyConfig();
            config.save();
            CobbleBounties.LOGGER.info("Created default config at: " + CONFIG_FILE.getAbsolutePath());
            return config;
        }
        
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            BountyConfig config = GSON.fromJson(reader, BountyConfig.class);
            CobbleBounties.LOGGER.info("Loaded config from: " + CONFIG_FILE.getAbsolutePath());
            return config;
        } catch (IOException e) {
            CobbleBounties.LOGGER.error("Failed to load config, using defaults", e);
            return new BountyConfig();
        }
    }
    
    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
            CobbleBounties.LOGGER.info("Saved config to: " + CONFIG_FILE.getAbsolutePath());
        } catch (IOException e) {
            CobbleBounties.LOGGER.error("Failed to save config", e);
        }
    }
}
