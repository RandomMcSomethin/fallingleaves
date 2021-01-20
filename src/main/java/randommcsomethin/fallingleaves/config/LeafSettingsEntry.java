package randommcsomethin.fallingleaves.config;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafSettingsEntry {

    public double spawnRateFactor;
    public boolean isConiferBlock;

    public LeafSettingsEntry(String identifier) {
        this.spawnRateFactor = ConfigDefaults.spawnRateFactor(identifier);
        this.isConiferBlock = ConfigDefaults.isConifer(identifier);
    }

    public double getSpawnChance() {
        double spawnChance = (isConiferBlock ? CONFIG.getBaseConiferLeafSpawnChance() : CONFIG.getBaseLeafSpawnChance());
        return spawnRateFactor * spawnChance;
    }

    @Override
    public String toString() {
        return String.format("LeafSettingsEntry{spawnRateFactor=%s, isConiferBlock=%s}",
            spawnRateFactor,
            isConiferBlock);
    }

}
