package com.cobblebounties.bounty;

import com.cobblebounties.CobbleBounties;
import com.cobblebounties.config.BountyConfig;
import com.cobblebounties.reward.EconomyManager;
import com.cobblebounties.reward.ItemRewardManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BountyManagerUpdated {
    private final BountyConfig config;
    private final List<Bounty> activeBounties;
    private final Random random;
    private MinecraftServer server;
    
    public BountyManagerUpdated(BountyConfig config) {
        this.config = config;
        this.activeBounties = new ArrayList<>();
        this.random = new Random();
    }
    
    public void loadBounties() {
        generateNewBounties();
        CobbleBounties.LOGGER.info("Generated {} initial bounties", activeBounties.size());
    }
    
    public void saveBounties() {
        CobbleBounties.LOGGER.info("Saving {} active bounties", activeBounties.size());
    }
    
    public void startBountyTimer(MinecraftServer server) {
        this.server = server;
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
        double roll = random.nextDouble();
        String rarity;
        List<String> pokemonPool;
        List<String> rewardPool;
        int moneyMultiplier;
        
        if (roll < 0.05) {
            rarity = "Legendary";
            pokemonPool = config.legendaryPokemon;
            rewardPool = config.legendaryRewards;
            moneyMultiplier = 10;
        } else if (roll < 0.20) {
            rarity = "Rare";
            pokemonPool = config.rarePokemon;
            rewardPool = config.rareRewards;
            moneyMultiplier = 5;
        } else if (roll < 0.50) {
            rarity = "Uncommon";
            pokemonPool = config.uncommonPokemon;
            rewardPool = config.uncommonRewards;
            moneyMultiplier = 2;
        } else {
            rarity = "Common";
            pokemonPool = config.commonPokemon;
            rewardPool = config.commonRewards;
            moneyMultiplier = 1;
        }
        
        String pokemon = pokemonPool.get(random.nextInt(pokemonPool.size()));
        int count = rarity.equals("Legendary") ? 1 : 
                   random.nextInt(config.maxCaptureCount - config.minCaptureCount + 1) + config.minCaptureCount;
        
        int money = config.baseMoneyReward * moneyMultiplier * count;
        
        List<Bounty.ItemReward> itemRewards = new ArrayList<>();
        int itemCount = random.nextInt(2) + 1;
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
                
                break;
            }
        }
    }
    
    private void rewardPlayer(ServerPlayer player, Bounty bounty) {
        // ===== DONNER L'ARGENT (NOUVEAU) =====
        EconomyManager.giveMoney(player, bounty.getMoneyReward())
            .thenAccept(success -> {
                if (success) {
                    CobbleBounties.LOGGER.info("Successfully gave ${} to {}", 
                        bounty.getMoneyReward(), 
                        player.getName().getString());
                } else {
                    CobbleBounties.LOGGER.warn("Failed to give money to {} - economy unavailable?", 
                        player.getName().getString());
                }
            });
        
        // ===== DONNER LES ITEMS (NOUVEAU) =====
        boolean itemsGiven = ItemRewardManager.giveRewards(player, bounty.getItemRewards());
        
        if (!itemsGiven) {
            player.sendSystemMessage(Component.literal(
                "§eNote: Some items were dropped on the ground (inventory full)"
            ));
        }
        
        // ===== NOTIFIER LE JOUEUR =====
        StringBuilder itemList = new StringBuilder();
        for (Bounty.ItemReward reward : bounty.getItemRewards()) {
            String itemName = reward.getItemId().replace("cobblemon:", "").replace("_", " ");
            itemList.append("\n  §7- §f").append(reward.getCount()).append("x §e").append(itemName);
        }
        
        player.sendSystemMessage(Component.literal(
            "§6§l╔═══════════════════════════╗\n" +
            "§6§l║  BOUNTY COMPLETED!      ║\n" +
            "§6§l╚═══════════════════════════╝\n" +
            "\n§ePokemon: §f" + bounty.getPokemonName().toUpperCase() + "\n" +
            "§eRarity: §f" + bounty.getRarity() + "\n" +
            "\n§6Rewards Received:\n" +
            "  §7- §a$" + bounty.getMoneyReward() + 
            itemList.toString() +
            "\n\n§aCheck your inventory!"
        ));
        
        // ===== BROADCAST (SI ACTIVÉ) =====
        if (config.broadcastCompletion && server != null) {
            server.getPlayerList().broadcastSystemMessage(
                Component.literal(
                    "§6" + player.getName().getString() + " §ecompleted a §l" + 
                    bounty.getRarity() + " §ebounty for §6" + bounty.getPokemonName() + "§e!"
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
