package com.cobblebounties.reward;

import com.cobblebounties.CobbleBounties;
import com.cobblebounties.bounty.Bounty;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de distribution d'items
 * Donne les items de récompense aux joueurs
 */
public class ItemRewardManager {
    
    /**
     * Donne tous les items d'une récompense à un joueur
     * 
     * @param player Le joueur à récompenser
     * @param rewards Liste des récompenses à donner
     * @return true si tous les items ont été donnés avec succès
     */
    public static boolean giveRewards(ServerPlayer player, List<Bounty.ItemReward> rewards) {
        List<ItemStack> failedItems = new ArrayList<>();
        int successCount = 0;
        
        for (Bounty.ItemReward reward : rewards) {
            ItemStack itemStack = createItemStack(reward);
            
            if (itemStack == null) {
                CobbleBounties.LOGGER.error("Failed to create item: {}", reward.getItemId());
                continue;
            }
            
            if (giveItemToPlayer(player, itemStack)) {
                successCount++;
                CobbleBounties.LOGGER.info("Gave {} x{} to {}", 
                    reward.getItemId(), 
                    reward.getCount(), 
                    player.getName().getString());
            } else {
                failedItems.add(itemStack);
                CobbleBounties.LOGGER.warn("Failed to give {} x{} to {} (inventory full?)", 
                    reward.getItemId(), 
                    reward.getCount(), 
                    player.getName().getString());
            }
        }
        
        // Si certains items n'ont pas pu être donnés, les drop au sol
        if (!failedItems.isEmpty()) {
            CobbleBounties.LOGGER.info("Dropping {} items on ground for {}", 
                failedItems.size(), 
                player.getName().getString());
            
            for (ItemStack itemStack : failedItems) {
                dropItemNearPlayer(player, itemStack);
            }
        }
        
        return successCount == rewards.size();
    }
    
    /**
     * Crée un ItemStack à partir d'une récompense
     * 
     * @param reward La récompense
     * @return ItemStack ou null si l'item n'existe pas
     */
    private static ItemStack createItemStack(Bounty.ItemReward reward) {
        try {
            ResourceLocation itemId = ResourceLocation.parse(reward.getItemId());
            Item item = BuiltInRegistries.ITEM.get(itemId);
            
            if (item == null) {
                CobbleBounties.LOGGER.error("Item not found in registry: {}", reward.getItemId());
                return null;
            }
            
            return new ItemStack(item, reward.getCount());
        } catch (Exception e) {
            CobbleBounties.LOGGER.error("Error creating item stack for: " + reward.getItemId(), e);
            return null;
        }
    }
    
    /**
     * Donne un item à un joueur (dans son inventaire)
     * 
     * @param player Le joueur
     * @param itemStack L'item à donner
     * @return true si l'item a été ajouté avec succès
     */
    private static boolean giveItemToPlayer(ServerPlayer player, ItemStack itemStack) {
        // Essayer d'ajouter à l'inventaire
        boolean added = player.getInventory().add(itemStack);
        
        if (added) {
            // Notifier le client que l'inventaire a changé
            player.containerMenu.broadcastChanges();
            return true;
        }
        
        return false;
    }
    
    /**
     * Drop un item près du joueur si son inventaire est plein
     * 
     * @param player Le joueur
     * @param itemStack L'item à dropper
     */
    private static void dropItemNearPlayer(ServerPlayer player, ItemStack itemStack) {
        player.drop(itemStack, false);
    }
    
    /**
     * Vérifie si un joueur a de la place dans son inventaire
     * 
     * @param player Le joueur
     * @param itemCount Nombre d'items à vérifier
     * @return true si le joueur a assez de place
     */
    public static boolean hasInventorySpace(ServerPlayer player, int itemCount) {
        int emptySlots = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).isEmpty()) {
                emptySlots++;
            }
        }
        return emptySlots >= itemCount;
    }
}
