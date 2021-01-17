package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import randommcsomethin.fallingleaves.FallingLeavesClient;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Config(name = FallingLeavesClient.MOD_ID)
public class FallingLeavesConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("fallingleaves.general")
    public boolean displayDebugData = false;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    private int leafSize = 5;

    // In 1.4 leafSize was a float with default 0.1 - effectively unchanged in 1.5
    public float getLeafSize() {
        return leafSize / 50F;
    }

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 100, max = 600)
    public int leafLifespan = 200;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 20)
    private int leafSpawnRate = 10;

    // 1.4 had a default spawn chance of 1.0 / 75 ~ 1.33% that could be set to 100%
    // 1.5 has the same default but is bounded to ~2.66% except when boosted by a spawn rate factor,
    // the maximum upper bound is currently ~26.6%
    public double getBaseLeafSpawnChance() {
        double actualSpawnRate = leafSpawnRate / 10.0;
        return actualSpawnRate / 75.0;
    }

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 20)
    private int coniferLeafSpawnRate = 0;

    public double getBaseConiferLeafSpawnChance() {
        double actualSpawnRate = coniferLeafSpawnRate / 10.0;
        return actualSpawnRate / 75.0;
    }

    @ConfigEntry.Category("fallingleaves.leafsettings")
    @ConfigEntry.Gui.TransitiveObject
    public LeafSettings leafSettings = new LeafSettings();

    public static class LeafSettings implements ConfigData {
        public Map<String, LeafSettingsEntry> entries = new HashMap<>();
    }
}