package randommcsomethin.fallingleaves.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.registry.Registry;
import randommcsomethin.fallingleaves.config.annotation.LeafBlockList;

import java.util.*;
import java.util.stream.Collectors;

public class OverrideConfiguration implements ConfigData {

    @LeafBlockList
    public ObjectLinkedOpenHashSet<OverrideEntry> overrideEntries;

    public OverrideConfiguration() {
    }

    @Override
    public void validatePostLoad() {
        final ObjectLinkedOpenHashSet<OverrideEntry> oldOverrideEntries = this.overrideEntries;

        // Load all registered leaf blocks
        this.overrideEntries = Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return block.getClass() == LeavesBlock.class || block.getClass().getSuperclass() == LeavesBlock.class;
            })
            .map(OverrideEntry::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));

        for (OverrideEntry entry : overrideEntries)
            setDefault(entry);

        // Apply overrides
        if (oldOverrideEntries != null) {
            for (OverrideEntry entry : oldOverrideEntries) {
                if (this.overrideEntries.contains(entry)) {
                    this.overrideEntries.remove(entry);
                    this.overrideEntries.add(entry);
                }
            }
        }
    }

    // TODO maybe we should move all this directly into the OverrideEntry constructor
    public static void setDefault(OverrideEntry entry) {
        entry.isConiferBlock = getDefaultIsConifer(entry);
        entry.spawnRate = getDefaultSpawnRate(entry);
        entry.useCustomSpawnRate = getDefaultUseCustomSpawnRate(entry);
    }

    public static boolean getDefaultIsConifer(OverrideEntry entry) {
        switch (entry.identifier) {
            // Conifer trees
            case "minecraft:spruce_leaves":
            case "terrestria:redwood_leaves":
            case "terrestria:hemlock_leaves":
            case "terrestria:cypress_leaves":
                return true;
            default:
                return false;
        }
    }

    public static int getDefaultSpawnRate(OverrideEntry entry) {
        switch (entry.identifier) {
            // Shrubs and large leaved trees
            case "minecraft:jungle_leaves":
            case "terrestria:japenese_maple_shrub_leaves":
            case "terrestria:yucca_palm_leaves":
            case "terrestria:jungle_palm_leaves":
                return 0;
            // For fun
            case "terrestria:sakura_leaves":
                return 2;
            default:
                return 1;
        }
    }

    public static boolean getDefaultUseCustomSpawnRate(OverrideEntry entry) {
        return (getDefaultSpawnRate(entry) != 1);
    }

}
