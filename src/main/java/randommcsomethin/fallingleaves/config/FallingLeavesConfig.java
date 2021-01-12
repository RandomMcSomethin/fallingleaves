package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.util.*;

@Config(name = "fallingleaves")
public class FallingLeavesConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("fallingleaves.general")
    public transient static FallingLeavesConfig instance;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 3)
    public int leafSize = 1;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 100, max = 500)
    public int leafLifespan = 200;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 10)
    public int leafSpawnRate = 1;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = 10)
    public int coniferLeafSpawnRate = 0;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Excluded
    public boolean displayDebugData = false;

    @ConfigEntry.Category("fallingleaves.overrides")
    @ConfigEntry.Gui.TransitiveObject
    public OverrideConfiguration override = new OverrideConfiguration();

    @Override
    public void validatePostLoad() {
        this.override.validatePostLoad();
    }

}