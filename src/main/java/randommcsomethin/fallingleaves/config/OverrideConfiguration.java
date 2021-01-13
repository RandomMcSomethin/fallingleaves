package randommcsomethin.fallingleaves.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import randommcsomethin.fallingleaves.config.annotation.LeafBlockList;

import java.util.stream.Collectors;

public class OverrideConfiguration implements ConfigData {

    @LeafBlockList
    public ObjectLinkedOpenHashSet<OverrideEntry> overrideEntries;

    public OverrideConfiguration() { }

    @Override
    public void validatePostLoad() {
        final ObjectLinkedOpenHashSet<OverrideEntry> oldOverrideEntries = this.overrideEntries;

        // Load all registered leaf blocks from Registry.
        this.overrideEntries = getRegisteredLeafBlocks();

        // Set overide defaults.
        for (OverrideEntry entry : overrideEntries) {
            entry.isConiferBlock = ConfigDefaults.isConifer(entry);
            entry.spawnRate = ConfigDefaults.spawnRate(entry);
            entry.useCustomSpawnRate = entry.spawnRate != 1;
        }

        // Apply override values from configuration file.
        if (oldOverrideEntries != null) {
            for (OverrideEntry entry : oldOverrideEntries) {
                if (this.overrideEntries.contains(entry)) {
                    this.overrideEntries.remove(entry);
                    this.overrideEntries.add(entry);
                }
            }
        }
    }

    @NotNull
    private ObjectLinkedOpenHashSet<OverrideEntry> getRegisteredLeafBlocks() {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return block.getClass() == LeavesBlock.class || block.getClass().getSuperclass() == LeavesBlock.class;
            })
            .map(OverrideEntry::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));
    }

}
