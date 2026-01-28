package com.cobblebounties.bounty;

import com.cobblebounties.CobbleBounties;
import com.cobblebounties.config.BountyConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BountyManager {
    private final BountyConfig config;
    private final List<Bounty> activeBounties;
    private final Random random;
    private MinecraftServer server;
    
    public BountyManager(BountyConfig config) {
        this.config = config;
        this.activeBounties = new ArrayList<>();
        this.random = new Random();
    }
    
    public void loadBounties() {
        // Generate initial bounties
        generateNewBounties();
        CobbleBounties.LOGGER.info("Generated {} initial bounties", activeBounties.size());
    }
    
    public void saveBounties() {
        // Save current bounties if needed (for now just log)
        CobbleBounties.LOGGER.info("Saving {} active bounties", activeBounties.size());
    }
    
    public void startBountyTimer(MinecraftServer server) {
        this.server = server;
        // Schedule bounty refresh
        Timer timer = new Timer();
        long refreshInterval = TimeUnit.HOURS.toMillis(config.bountyDurationHours);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshBounties();
            }
        }, refreshInterval, refreshInterval);
    }
    
    public void refreshBounties() {
        activeBounties.clear();
        generateNewBounties();
        
        if (config.broadcastNewBounties && server != null) {
            broadcastNewBounties();
        }
        
        CobbleBounties.LOGGER.info("Refreshed bounties - {} new bounties active", activeBounties.size());
    }
    
    private void generateNewBounties() {
        for (int i = 0; i < config.activeBountyCount; i++) {
            Bounty bounty = generateRandomBounty();
            activeBounties.add(bounty);
        }
    }
    
    private Bounty generateRandomBounty() {
        // Determine rarity
        double roll = random.nextDouble();
        String rarity;
        List<String> pokemonPool;
        List<String> rewardPool;
        int moneyMultiplier;
        
        if (roll < 0.05) { // 5% legendary
            rarity = "Legendary";
            pokemonPool = config.legendaryPokemon;
            rewardPool = config.legendaryRewards;
            moneyMultiplier = 10;
        } else if (roll < 0.20) { // 15% rare
            rarity = "Rare";
            pokemonPool = config.rarePokemon;
            rewardPool = config.rareRewards;
            moneyMultiplier = 5;
        } else if (roll < 0.50) { // 30% uncommon
            rarity = "Uncommon";
            pokemonPool = config.uncommonPokemon;
            rewardPool = config.uncommonRewards;
            moneyMultiplier = 2;
        } else { // 50% common
            rarity = "Common";
            pokemonPool = config.commonPokemon;
            rewardPool = config.commonRewards;
            moneyMultiplier = 1;
        }
        
        // Select random pokemon
        String pokemon = pokemonPool.get(random.nextInt(pokemonPool.size()));
        
        // Determine count based on rarity
        int count = rarity.equals("Legendary") ? 1 : 
                   random.nextInt(config.maxCaptureCount - config.minCaptureCount + 1) + config.minCaptureCount;
        
        // Calculate rewards
        int money = config.baseMoneyReward * moneyMultiplier * count;
        
        // Select 1-2 random item rewards
        List<Bounty.ItemReward> itemRewards = new ArrayList<>();
        int itemCount = random.nextInt(2) + 1; // 1 or 2 items
        for (int i = 0; i < itemCount; i++) {
            String rewardString = rewardPool.get(random.nextInt(rewardPool.size()));
            try {
                itemRewards.add(Bounty.ItemReward.parse(rewardString));
            } catch (Exception e) {
                CobbleBounties.LOGGER.error("Failed to parse reward: " + rewardString, e);
            }
        }
        
        long durationMillis = TimeUnit.HOURS.toMillis(config.bountyDurationHours);
        
        return new Bounty(pokemon, count, money, itemRewards, durationMillis, rarity);
    }
    
    public List<Bounty> getActiveBounties() {
        // Remove expired bounties
        activeBounties.removeIf(Bounty::isExpired);
        return new ArrayList<>(activeBounties);
    }
    
    public void onPokemonCaptured(ServerPlayer player, String pokemonName) {
        UUID playerId = player.getUUID();
        
        for (Bounty bounty : getActiveBounties()) {
            if (bounty.getPokemonName().equalsIgnoreCase(pokemonName) && !bounty.isComplete(playerId)) {
                bounty.addProgress(playerId, 1);
                
                int progress = bounty.getProgress(playerId);
                int required = bounty.getRequiredCount();
                
                player.sendSystemMessage(Component.literal(
                    "§6[Bounty] §eProgress: " + pokemonName + " " + progress + "/" + required
                ));
                
                if (bounty.isComplete(playerId)) {
                    rewardPlayer(player, bounty);
                }
                
                break; // Only count for one bounty
            }
        }
    }
    
    private void rewardPlayer(ServerPlayer player, Bounty bounty) {
        // Give money reward
        // Note: This would need integration with an economy mod like Impactor
        // For now, just log it
        CobbleBounties.LOGGER.info("Player {} completed bounty for {} - Reward: ${}",
            player.getName().getString(), bounty.getPokemonName(), bounty.getMoneyReward());
        
        // Give item rewards
        for (Bounty.ItemReward reward : bounty.getItemRewards()) {
            // TODO: Give actual items - would need proper item registry access
            CobbleBounties.LOGGER.info("Giving {} x{} to {}", 
                reward.getItemId(), reward.getCount(), player.getName().getString());
        }
        
        // Notify player
        player.sendSystemMessage(Component.literal(
            "§6§l[BOUNTY COMPLETED!]\n" +
            "§ePokemon: §f" + bounty.getPokemonName() + "\n" +
            "§eReward: §a$" + bounty.getMoneyReward() + " §e+ Items\n" +
            "§7Check your inventory!"
        ));
        
        // Broadcast if enabled
        if (config.broadcastCompletion && server != null) {
            server.getPlayerList().broadcastSystemMessage(
                Component.literal(
                    "§6" + player.getName().getString() + " §ecompleted a " + 
                    bounty.getRarity() + " bounty for §6" + bounty.getPokemonName() + "§e!"
                ),
                false
            );
        }
    }
    
    private void broadcastNewBounties() {
        if (server == null) return;
        
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l[NEW BOUNTIES AVAILABLE!]\n§eUse /bounties to see active hunts!"),
            false
        );
    }
}
