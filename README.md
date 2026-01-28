# Cobblemon Bounties

A lightweight bounty hunting system for Cobblemon 1.7.1+ servers!

## Features

✨ **Dynamic Bounty System**
- 3 active bounties at a time (configurable)
- Bounties refresh every 24 hours (configurable)
- 4 rarity tiers: Common, Uncommon, Rare, Legendary

💰 **Rewards**
- Money rewards (scales with rarity and capture count)
- Item rewards (Poké Balls, Potions, Evolution Stones, Rare Candies, etc.)
- Multiple items per bounty completion

📊 **Player Tracking**
- Individual progress tracking
- See your progress with `/bounties` command
- Completion notifications

🎯 **Automatic Detection**
- Bounties auto-complete when you capture the target Pokemon
- No manual claiming needed!

## Installation

1. Download the `.jar` file
2. Place it in your server's `/mods` folder
3. Restart the server
4. Configure in `config/cobblebounties.json`

## Commands

- `/bounties` or `/bounty` - View all active bounties and your progress

## Configuration

After first run, edit `config/cobblebounties.json` to customize:

```json
{
  "activeBountyCount": 3,           // Number of simultaneous bounties
  "bountyDurationHours": 24,        // How long before bounties refresh
  "minCaptureCount": 1,             // Minimum captures required
  "maxCaptureCount": 5,             // Maximum captures required
  "baseMoneyReward": 500,           // Base money reward (multiplied by rarity)
  "broadcastNewBounties": true,     // Announce new bounties to all players
  "broadcastCompletion": true,      // Announce when someone completes a bounty
  
  // Customize reward pools for each rarity
  "commonRewards": [...],
  "uncommonRewards": [...],
  "rareRewards": [...],
  "legendaryRewards": [...],
  
  // Customize Pokemon pools for each rarity
  "commonPokemon": [...],
  "uncommonPokemon": [...],
  "rarePokemon": [...],
  "legendaryPokemon": [...]
}
```

## Rarity System

| Rarity | Chance | Money Multiplier | Example Rewards |
|--------|--------|------------------|-----------------|
| Common | 50% | 1x | Poké Balls, Potions |
| Uncommon | 30% | 2x | Great Balls, Super Potions, Rare Candy |
| Rare | 15% | 5x | Ultra Balls, Evolution Stones |
| Legendary | 5% | 10x | Master Ball, Ability Capsule |

## Compatibility

- ✅ Fabric 1.21.1
- ✅ Cobblemon 1.7.1+
- ✅ Works with Abes Hutts Cobblemon fork
- ⚠️ Requires Fabric API

## TODO / Future Features

- [ ] Economy integration (currently logs money rewards)
- [ ] Actual item giving (currently logs items)
- [ ] Admin commands to force refresh bounties
- [ ] Player statistics tracking
- [ ] Leaderboards
- [ ] Custom bounty creation via commands

## Known Limitations

**Current Version (1.0.0):**
- Money rewards are logged but not actually given (needs economy mod integration)
- Item rewards are logged but not actually given to inventory (needs proper item registry)
- This is a **proof of concept** version

**To make it fully functional**, you would need to:
1. Integrate with an economy system (like Impactor's economy)
2. Add proper item spawning using Minecraft's item registry

## Building from Source

```bash
./gradlew build
```

The compiled `.jar` will be in `build/libs/`

## License

MIT License - Feel free to modify and distribute!

## Credits

Created by Claude for the Pokérayou S2 server project 🎮
