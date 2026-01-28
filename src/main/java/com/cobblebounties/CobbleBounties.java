package com.cobblebounties;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobblebounties.bounty.BountyManagerUpdated;
import com.cobblebounties.command.BountyCommand;
import com.cobblebounties.config.BountyConfig;
import com.cobblebounties.event.PokemonCaptureHandler;
import com.cobblebounties.reward.EconomyManager;

public class CobbleBounties implements ModInitializer {
    public static final String MOD_ID = "cobblebounties";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static BountyManagerUpdated bountyManager;
    private static BountyConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Cobblemon Bounties...");
        
        // Load config
        config = BountyConfig.load();
        
        // Initialize economy system (gracefully fails if Impactor not present)
        EconomyManager.initialize();
        
        // Initialize bounty manager
        bountyManager = new BountyManagerUpdated(config);
        
        // Register commands
        BountyCommand.register();
        
        // Register event handlers
        PokemonCaptureHandler.register();
        
        // Server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Server started - Loading bounties...");
            bountyManager.loadBounties();
            bountyManager.startBountyTimer(server);
        });
        
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("Server stopping - Saving bounties...");
            bountyManager.saveBounties();
        });
        
        LOGGER.info("Cobblemon Bounties initialized successfully!");
    }
    
    public static BountyManagerUpdated getBountyManager() {
        return bountyManager;
    }
    
    public static BountyConfig getConfig() {
        return config;
    }
}
