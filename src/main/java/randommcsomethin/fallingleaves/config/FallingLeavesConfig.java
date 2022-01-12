package randommcsomethin.fallingleaves.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Identifier;
import randommcsomethin.fallingleaves.FallingLeavesClient;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Config(name = FallingLeavesClient.MOD_ID)
public class FallingLeavesConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("fallingleaves.general")
    public int version = 1;

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("fallingleaves.general")
    public boolean displayDebugData = false;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    private int leafSize = 5;

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

    @ConfigEntry.Category("fallingleaves.general")
    public boolean dropFromPlayerPlacedBlocks = true;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    public int minimumFreeSpaceBelow = 1;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    public boolean windEnabled = true;

    @ConfigEntry.Category("fallingleaves.general")
    public Set<Identifier> windlessDimensions = new HashSet<>(Arrays.asList(
        new Identifier("the_nether"), new Identifier("the_end")));

    @ConfigEntry.Category("fallingleaves.leafsettings")
    @ConfigEntry.Gui.TransitiveObject
    public Map<Identifier, LeafSettingsEntry> leafSettings = new HashMap<>();

    @ConfigEntry.Category("fallingleaves.experimental")
    @ConfigEntry.Gui.Tooltip
    public Set<Identifier> leafSpawners = new HashSet<>();

    public void updateLeafSettings(Identifier blockId, Consumer<LeafSettingsEntry> f) {
        leafSettings.compute(blockId, (id, entry) -> {
            if (entry == null)
                entry = new LeafSettingsEntry(id);

            f.accept(entry);

            return entry;
        });
    }

    /* Setters only used for config migration right now */

    /** Inverse of getLeafSize() */
    public void setLeafSize(double leafSize) {
        this.leafSize = (int)(leafSize * 50.0);
    }

    /** Inverse of "actualSpawnRate" in getBaseLeafSpawnChance() */
    public void setLeafSpawnRate(double leafRate) {
        leafSpawnRate = (int)(leafRate * 10.0);
    }

    /** Inverse of "actualSpawnRate" in getBaseConiferLeafSpawnChance() */
    public void setConiferLeafSpawnRate(double coniferLeafRate) {
        coniferLeafSpawnRate = (int)(coniferLeafRate * 10.0);
    }

    @Override
    public void validatePostLoad() throws ConfigData.ValidationException {
        version = 1;
        leafSize = Math.max(leafSize, 1);
        minimumFreeSpaceBelow = Math.max(minimumFreeSpaceBelow, 1);

        for (var spawner : leafSpawners)
            leafSettings.computeIfAbsent(spawner, LeafSettingsEntry::new);
    }

}