package com.cobblebounties.reward;

import com.cobblebounties.CobbleBounties;
import net.minecraft.server.level.ServerPlayer;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Gestionnaire d'économie
 * Note: Nécessite Impactor sur le serveur pour fonctionner
 * Si Impactor n'est pas présent, les récompenses monétaires seront simplement loggées
 */
public class EconomyManager {
    
    private static Object economyService = null;
    private static Object primaryCurrency = null;
    private static boolean impactorAvailable = false;
    
    /**
     * Initialise le gestionnaire d'économie
     * Appeler au démarrage du serveur
     */
    public static void initialize() {
        try {
            // Tentative de chargement d'Impactor via réflexion
            Class<?> economyServiceClass = Class.forName("net.impactdev.impactor.api.economy.EconomyService");
            java.lang.reflect.Method instanceMethod = economyServiceClass.getMethod("instance");
            economyService = instanceMethod.invoke(null);
            
            // Obtenir la devise principale
            java.lang.reflect.Method currenciesMethod = economyServiceClass.getMethod("currencies");
            Object currencies = currenciesMethod.invoke(economyService);
            java.lang.reflect.Method primaryMethod = currencies.getClass().getMethod("primary");
            primaryCurrency = primaryMethod.invoke(currencies);
            
            impactorAvailable = true;
            CobbleBounties.LOGGER.info("Economy integration initialized successfully with Impactor");
        } catch (ClassNotFoundException e) {
            CobbleBounties.LOGGER.info("Impactor not found - monetary rewards will be disabled");
            CobbleBounties.LOGGER.info("Install Impactor to enable money rewards: https://impactdev.net/");
            impactorAvailable = false;
        } catch (Exception e) {
            CobbleBounties.LOGGER.error("Failed to initialize economy integration", e);
            impactorAvailable = false;
        }
    }
    
    /**
     * Vérifie si l'économie est disponible
     */
    public static boolean isAvailable() {
        return impactorAvailable && economyService != null && primaryCurrency != null;
    }
    
    /**
     * Donne de l'argent à un joueur
     * 
     * @param player Le joueur à récompenser
     * @param amount Le montant à donner
     * @return CompletableFuture indiquant le succès
     */
    public static CompletableFuture<Boolean> giveMoney(ServerPlayer player, int amount) {
        if (!isAvailable()) {
            CobbleBounties.LOGGER.warn("Economy not available - monetary reward not given");
            CobbleBounties.LOGGER.info("Player {} would have received ${} (install Impactor to enable)", 
                player.getName().getString(), amount);
            return CompletableFuture.completedFuture(false);
        }
        
        try {
            UUID playerId = player.getUUID();
            BigDecimal amountDecimal = BigDecimal.valueOf(amount);
            
            // Utilisation de la réflexion pour appeler Impactor
            java.lang.reflect.Method accountMethod = economyService.getClass()
                .getMethod("account", Object.class, UUID.class);
            
            @SuppressWarnings("unchecked")
            CompletableFuture<Object> accountFuture = 
                (CompletableFuture<Object>) accountMethod.invoke(economyService, primaryCurrency, playerId);
            
            return accountFuture.thenCompose(accountOpt -> {
                try {
                    // Vérifier si le compte existe
                    java.lang.reflect.Method isEmptyMethod = accountOpt.getClass().getMethod("isEmpty");
                    boolean isEmpty = (boolean) isEmptyMethod.invoke(accountOpt);
                    
                    if (isEmpty) {
                        CobbleBounties.LOGGER.error("Could not find account for player: {}", 
                            player.getName().getString());
                        return CompletableFuture.completedFuture(false);
                    }
                    
                    // Obtenir le compte
                    java.lang.reflect.Method getMethod = accountOpt.getClass().getMethod("get");
                    Object account = getMethod.invoke(accountOpt);
                    
                    // Déposer l'argent
                    java.lang.reflect.Method depositMethod = account.getClass()
                        .getMethod("deposit", BigDecimal.class);
                    
                    @SuppressWarnings("unchecked")
                    CompletableFuture<Object> depositFuture = 
                        (CompletableFuture<Object>) depositMethod.invoke(account, amountDecimal);
                    
                    return depositFuture.thenApply(result -> {
                        try {
                            java.lang.reflect.Method successfulMethod = result.getClass()
                                .getMethod("successful");
                            boolean successful = (boolean) successfulMethod.invoke(result);
                            
                            if (successful) {
                                CobbleBounties.LOGGER.info("Gave ${} to {}", 
                                    amount, player.getName().getString());
                                return true;
                            } else {
                                CobbleBounties.LOGGER.error("Failed to deposit money");
                                return false;
                            }
                        } catch (Exception e) {
                            CobbleBounties.LOGGER.error("Error processing deposit result", e);
                            return false;
                        }
                    });
                } catch (Exception e) {
                    CobbleBounties.LOGGER.error("Error accessing account", e);
                    return CompletableFuture.completedFuture(false);
                }
            }).exceptionally(throwable -> {
                CobbleBounties.LOGGER.error("Error giving money to player", throwable);
                return false;
            });
        } catch (Exception e) {
            CobbleBounties.LOGGER.error("Error in giveMoney", e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    /**
     * Obtient le solde d'un joueur
     * 
     * @param playerId UUID du joueur
     * @return CompletableFuture avec le solde
     */
    public static CompletableFuture<BigDecimal> getBalance(UUID playerId) {
        if (!isAvailable()) {
            return CompletableFuture.completedFuture(BigDecimal.ZERO);
        }
        
        try {
            java.lang.reflect.Method accountMethod = economyService.getClass()
                .getMethod("account", Object.class, UUID.class);
            
            @SuppressWarnings("unchecked")
            CompletableFuture<Object> accountFuture = 
                (CompletableFuture<Object>) accountMethod.invoke(economyService, primaryCurrency, playerId);
            
            return accountFuture.thenCompose(accountOpt -> {
                try {
                    java.lang.reflect.Method isEmptyMethod = accountOpt.getClass().getMethod("isEmpty");
                    boolean isEmpty = (boolean) isEmptyMethod.invoke(accountOpt);
                    
                    if (isEmpty) {
                        return CompletableFuture.completedFuture(BigDecimal.ZERO);
                    }
                    
                    java.lang.reflect.Method getMethod = accountOpt.getClass().getMethod("get");
                    Object account = getMethod.invoke(accountOpt);
                    
                    java.lang.reflect.Method balanceMethod = account.getClass().getMethod("balance");
                    
                    @SuppressWarnings("unchecked")
                    CompletableFuture<BigDecimal> balanceFuture = 
                        (CompletableFuture<BigDecimal>) balanceMethod.invoke(account);
                    
                    return balanceFuture;
                } catch (Exception e) {
                    CobbleBounties.LOGGER.error("Error getting balance", e);
                    return CompletableFuture.completedFuture(BigDecimal.ZERO);
                }
            });
        } catch (Exception e) {
            CobbleBounties.LOGGER.error("Error in getBalance", e);
            return CompletableFuture.completedFuture(BigDecimal.ZERO);
        }
    }
}
