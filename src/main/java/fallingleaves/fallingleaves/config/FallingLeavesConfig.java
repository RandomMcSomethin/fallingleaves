package fallingleaves.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Config(name = "fallingleaves")
public class FallingLeavesConfig implements ConfigData {

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    public double leafSize = 0.10;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    public int leafLifespan = 200;

    @ConfigEntry.Category("fallingleaves.general")
    @ConfigEntry.Gui.Tooltip
    public double leafRate = 1.0;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("fallingleaves.general")
    public double coniferLeafRate = 0.0;

    @ConfigEntry.Category("fallingleaves.conifer")
    @ConfigEntry.Gui.Tooltip
    public Set<String> coniferLeafIds = new TreeSet<>(Arrays.asList(
        // Conifer trees
        "minecraft:spruce_leaves",
        "terrestria:redwood_leaves",
        "terrestria:hemlock_leaves",
        "terrestria:cypress_leaves",
        "traverse:fir_leaves"
    ));

    @ConfigEntry.Category("fallingleaves.overrides")
    @ConfigEntry.Gui.Tooltip
    public Map<String, Double> rateOverrides = new TreeMap<String, Double>() {{
        // Shrubs
        put("minecraft:jungle_leaves", 0.0);
        put("terrestria:japenese_maple_shrub_leaves", 0.0);
        // Large leaved trees
        put("terrestria:yucca_palm_leaves", 0.0);
        put("terrestria:jungle_palm_leaves", 0.0);
        // For fun
        put("terrestria:sakura_leaves", 2.0);
    }};
}