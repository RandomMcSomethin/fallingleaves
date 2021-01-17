package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Identifier;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class ConfigUtil {

    public static LeafSettingsEntry getLeafSettingsConfig(String blockId) {
        return CONFIG.leafSettings.entries.get(new LeafSettingsEntry(new Identifier(blockId)));
    }

    public static boolean shouldDebugDataBeDisplayed() {
        return CONFIG.displayDebugData;
    }

}
