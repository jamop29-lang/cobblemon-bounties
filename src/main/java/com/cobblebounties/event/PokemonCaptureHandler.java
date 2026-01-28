package com.cobblebounties.event;

import com.cobblebounties.CobbleBounties;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import net.minecraft.server.level.ServerPlayer;
import kotlin.Unit;

public class PokemonCaptureHandler {
    
    public static void register() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {
            handlePokemonCapture(event);
            return Unit.INSTANCE;
        });
        
        CobbleBounties.LOGGER.info("Registered Pokemon capture event handler");
    }
    
    private static void handlePokemonCapture(PokemonCapturedEvent event) {
        try {
            if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
                // Get the pokemon species name
                String pokemonName = event.getPokemon().getSpecies().getName();
                
                CobbleBounties.LOGGER.debug("Player {} captured: {}", 
                    serverPlayer.getName().getString(), pokemonName);
                
                // Check if this capture completes a bounty
                CobbleBounties.getBountyManager().onPokemonCaptured(serverPlayer, pokemonName);
            }
        } catch (Exception e) {
            CobbleBounties.LOGGER.error("Error handling pokemon capture event", e);
        }
    }
}
