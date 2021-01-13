package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OverrideEntry {
    public final String identifier;

    public int spawnRate;
    public boolean isConiferBlock;
    public boolean useGlobalRate;

    @ConfigEntry.Gui.Excluded
    private transient Block block;

    public OverrideEntry(final Identifier identifier) {
        this.identifier = identifier.toString();
        this.block = Registry.BLOCK.get(identifier);
        this.spawnRate = 1;
        this.useGlobalRate = true;
        this.isConiferBlock = false;
    }

    public Block getBlock() {
        return this.block == null ? this.block = Registry.BLOCK.get(new Identifier(this.identifier)) : this.block;
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof OverrideEntry)) return false;
        OverrideEntry that = (OverrideEntry) o;
        return identifier.equals(that.identifier);
    }

}
