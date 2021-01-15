package randommcsomethin.fallingleaves;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.util.LogUtil;

@Environment(EnvType.CLIENT)
public class FallingLeavesClient implements ClientModInitializer {

    // The mod's unique identifier, used when interacting with the registry.
    public static String MOD_ID = "fallingleaves";

    // The logger object, a facade/proxy for Log4j. Singleton here for ease of use.
    public static LogUtil LOGGER = new LogUtil(MOD_ID);

    @Override
    public void onInitializeClient() {
        Config.init();
        Leaves.init();
    }
}
