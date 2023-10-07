package randommcsomethin.fallingleaves.config;

import net.minecraft.util.Identifier;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafSettingsEntry {

    public double spawnRateFactor;
    public boolean isConiferBlock;
    public boolean spawnBreakingLeaves;

    public LeafSettingsEntry(Identifier identifier) {
        this.spawnRateFactor = ConfigDefaults.spawnRateFactor(identifier);
        this.isConiferBlock = ConfigDefaults.isConifer(identifier);
        this.spawnBreakingLeaves = ConfigDefaults.spawnBreakingLeaves(identifier);
    }

    public double getSpawnChance() {
        double spawnChance = (isConiferBlock ? CONFIG.getBaseConiferLeafSpawnChance() : CONFIG.getBaseLeafSpawnChance());
        return spawnRateFactor * spawnChance;
    }

    public boolean isDefault(Identifier identifier) {
        return spawnRateFactor == ConfigDefaults.spawnRateFactor(identifier)
            && isConiferBlock == ConfigDefaults.isConifer(identifier)
            && spawnBreakingLeaves == ConfigDefaults.spawnBreakingLeaves(identifier);
    }

    @Override
    public String toString() {
        return String.format("LeafSettingsEntry{spawnRateFactor=%s, isConiferBlock=%s, spawnBreakingLeaves=%s}",
            spawnRateFactor,
            isConiferBlock,
            spawnBreakingLeaves);
    }
}
