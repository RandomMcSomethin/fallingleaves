package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OverrideEntry {
    public final String identifier;

    public int rate;
    public boolean isConiferBlock;
    public boolean useGlobalRate;

    @ConfigEntry.Gui.Excluded
    private transient Block block;

    public OverrideEntry() {
        this.identifier = null;
    }

    public OverrideEntry(final Identifier identifier) {
        this.identifier = identifier.toString();
        this.block = Registry.BLOCK.get(identifier);
        this.rate = 1;
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
    public boolean equals(final Object that) {
        return that instanceof OverrideEntry && that.hashCode() == this.hashCode();
    }

}
