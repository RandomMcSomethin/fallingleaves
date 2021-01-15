package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Identifier;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

/**
 * A fairly pointless class where we can set up static methods to
 * query individual configuration options without having to call
 * the actual configuration object every time.
 */
public class ConfigUtil {

    /**
     * Get the LeafSettingsEntry object for any given Leaf block.
     * @param blockId The name of the leaf block to look up.
     * @return LeafSettingsEntry
     */
    public static LeafSettingsEntry getLeafSettingsConfig(String blockId) {
        return CONFIG.leafSettings.entries.get(new LeafSettingsEntry(new Identifier(blockId)));
    }

    public static boolean shouldDebugDataBeDisplayed() {
        return CONFIG.displayDebugData;
    }

}
