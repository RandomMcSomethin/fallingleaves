package randommcsomethin.fallingleaves.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import randommcsomethin.fallingleaves.config.annotation.LeafBlockList;

import java.util.stream.Collectors;

public class LeafSettings implements ConfigData {

    @LeafBlockList
    public ObjectLinkedOpenHashSet<LeafSettingsEntry> entries;

    public LeafSettings() { }

    @Override
    public void validatePostLoad() {
        ObjectLinkedOpenHashSet<LeafSettingsEntry> oldEntries = entries;

        // TODO:
        //  fundamental issue: other mods may not yet have registered their blocks,
        //  so entries will be missing.
        //  We may need to postpone getRegisteredLeafBlocks() or alternatively add entries as leaves are discovered

        // Load all registered leaf blocks from Registry
        entries = getRegisteredLeafBlocks();

        // Set defaults
        for (LeafSettingsEntry entry : entries) {
            entry.isConiferBlock = ConfigDefaults.isConifer(entry);
            entry.spawnRate = ConfigDefaults.spawnRate(entry);
            entry.useCustomSpawnRate = ConfigDefaults.useCustomSpawnRate(entry);
        }

        // Insert/Apply values from configuration file
        if (oldEntries != null) {
            for (LeafSettingsEntry entry : oldEntries) {
                entries.remove(entry);
                entries.add(entry);
            }
        }
    }

    @NotNull
    private ObjectLinkedOpenHashSet<LeafSettingsEntry> getRegisteredLeafBlocks() {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return block instanceof LeavesBlock;
            })
            .map(LeafSettingsEntry::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));
    }

}
