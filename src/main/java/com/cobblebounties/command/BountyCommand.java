package com.cobblebounties.command;

import com.cobblebounties.CobbleBounties;
import com.cobblebounties.bounty.Bounty;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class BountyCommand {
    
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerBountiesCommand(dispatcher);
        });
    }
    
    private static void registerBountiesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("bounties")
                .executes(BountyCommand::listBounties)
        );
        
        dispatcher.register(
            Commands.literal("bounty")
                .executes(BountyCommand::listBounties)
        );
    }
    
    private static int listBounties(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        List<Bounty> bounties = CobbleBounties.getBountyManager().getActiveBounties();
        
        if (bounties.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cNo active bounties at the moment!"));
            return 0;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("§6§l========== ACTIVE BOUNTIES ==========\n");
        
        for (int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            int progress = bounty.getProgress(player.getUUID());
            
            String rarityColor = switch (bounty.getRarity()) {
                case "Legendary" -> "§5§l";
                case "Rare" -> "§9";
                case "Uncommon" -> "§a";
                default -> "§f";
            };
            
            message.append("\n§e").append(i + 1).append(". ")
                   .append(rarityColor).append(bounty.getPokemonName().toUpperCase())
                   .append(" §7(").append(bounty.getRarity()).append(")\n");
            
            message.append("   §7Target: §f").append(bounty.getRequiredCount())
                   .append(" captures\n");
            
            message.append("   §7Progress: §e").append(progress)
                   .append("§7/§e").append(bounty.getRequiredCount());
            
            if (bounty.isComplete(player.getUUID())) {
                message.append(" §a✓ COMPLETED");
            }
            
            message.append("\n   §7Reward: §a$").append(bounty.getMoneyReward())
                   .append(" §7+ §6").append(bounty.getItemRewards().size())
                   .append(" items\n");
            
            message.append("   §7Expires in: §c").append(bounty.getTimeRemainingFormatted())
                   .append("\n");
        }
        
        message.append("§6§l=====================================");
        
        player.sendSystemMessage(Component.literal(message.toString()));
        return 1;
    }
}
