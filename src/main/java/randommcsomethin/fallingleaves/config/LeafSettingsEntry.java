package randommcsomethin.fallingleaves.config;

public class LeafSettingsEntry {

    public double spawnRateFactor;
    public boolean isConiferBlock;

    public LeafSettingsEntry(String identifier) {
        this.spawnRateFactor = ConfigDefaults.spawnRateFactor(identifier);
        this.isConiferBlock = ConfigDefaults.isConifer(identifier);
    }

    @Override
    public String toString() {
        return String.format("LeafSettingsEntry{spawnRateFactor=%s, isConiferBlock=%s}",
            spawnRateFactor,
            isConiferBlock);
    }

}
