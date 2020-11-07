package fallingleaves.fallingleaves;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "fallingleaves")
public class FallingLeavesConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public double leafSize = 0.10;

}