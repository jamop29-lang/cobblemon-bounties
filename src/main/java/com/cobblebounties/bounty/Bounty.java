package com.cobblebounties.bounty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Bounty {
    private final String pokemonName;
    private final int requiredCount;
    private final int moneyReward;
    private final List<ItemReward> itemRewards;
    private final long expirationTime;
    private final Map<UUID, Integer> playerProgress;
    private final String rarity;
    
    public Bounty(String pokemonName, int requiredCount, int moneyReward, 
                  List<ItemReward> itemRewards, long durationMillis, String rarity) {
        this.pokemonName = pokemonName;
        this.requiredCount = requiredCount;
        this.moneyReward = moneyReward;
        this.itemRewards = itemRewards;
        this.expirationTime = System.currentTimeMillis() + durationMillis;
        this.playerProgress = new HashMap<>();
        this.rarity = rarity;
    }
    
    public String getPokemonName() {
        return pokemonName;
    }
    
    public int getRequiredCount() {
        return requiredCount;
    }
    
    public int getMoneyReward() {
        return moneyReward;
    }
    
    public List<ItemReward> getItemRewards() {
        return itemRewards;
    }
    
    public long getExpirationTime() {
        return expirationTime;
    }
    
    public String getRarity() {
        return rarity;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }
    
    public int getProgress(UUID playerId) {
        return playerProgress.getOrDefault(playerId, 0);
    }
    
    public void addProgress(UUID playerId, int amount) {
        int current = getProgress(playerId);
        playerProgress.put(playerId, Math.min(current + amount, requiredCount));
    }
    
    public boolean isComplete(UUID playerId) {
        return getProgress(playerId) >= requiredCount;
    }
    
    public long getTimeRemaining() {
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }
    
    public String getTimeRemainingFormatted() {
        long millis = getTimeRemaining();
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
    
    public static class ItemReward {
        private final String itemId;
        private final int count;
        
        public ItemReward(String itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }
        
        public String getItemId() {
            return itemId;
        }
        
        public int getCount() {
            return count;
        }
        
        public static ItemReward parse(String rewardString) {
            // Format: "cobblemon:poke_ball:3"
            String[] parts = rewardString.split(":");
            if (parts.length == 3) {
                String itemId = parts[0] + ":" + parts[1];
                int count = Integer.parseInt(parts[2]);
                return new ItemReward(itemId, count);
            }
            throw new IllegalArgumentException("Invalid reward format: " + rewardString);
        }
    }
}
