package randommcsomethin.fallingleaves;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.init.Leaves;

@Environment(EnvType.CLIENT)
public class FallingLeavesClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        Config.init();
        Leaves.init();
    }

}
