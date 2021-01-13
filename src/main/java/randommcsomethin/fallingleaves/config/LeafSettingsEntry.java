package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;

public class LeafSettingsEntry {
    // Comparing by translation is incompatible with the equals() below, so we use a Comparator instead
    public static class TranslationComparator implements Comparator<LeafSettingsEntry> {
        public static final TranslationComparator INSTANCE = new TranslationComparator();

        private TranslationComparator() { }

        @Override
        public int compare(LeafSettingsEntry o1, LeafSettingsEntry o2) {
            return o1.getTranslation().compareTo(o2.getTranslation());
        }
    }

    public final String identifier;

    public int spawnRate;
    public boolean isConiferBlock;
    public boolean useCustomSpawnRate;

    @ConfigEntry.Gui.Excluded
    private transient Block block = null;
    @ConfigEntry.Gui.Excluded
    private transient String cachedTranslation = null;
    @ConfigEntry.Gui.Excluded
    private transient Language cachedLanguage = null;

    public LeafSettingsEntry(Identifier identifier) {
        this.identifier = identifier.toString();
    }

    public Block getBlock() {
        // Block might be registered after this mod, so we defer the registry lookup
        if (block == null)
            block = Registry.BLOCK.get(new Identifier(identifier));

        return block;
    }

    public String getTranslation() {
        Language language = Language.getInstance();

        // Update translation when needed
        if (cachedTranslation == null || language != cachedLanguage) {
            cachedLanguage = language;
            cachedTranslation = language.get(getBlock().getTranslationKey());
            return cachedTranslation;
        }

        return cachedTranslation;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeafSettingsEntry)) return false;
        LeafSettingsEntry that = (LeafSettingsEntry) o;
        return this.identifier.equals(that.identifier);
    }

    @Override
    public String toString() {
        return "LeafSettingsEntry{" +
            "identifier='" + identifier + '\'' +
            ", spawnRate=" + spawnRate +
            ", isConiferBlock=" + isConiferBlock +
            ", useCustomSpawnRate=" + useCustomSpawnRate +
            '}';
    }

}
