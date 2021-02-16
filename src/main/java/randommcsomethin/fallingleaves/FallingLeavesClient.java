package randommcsomethin.fallingleaves;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.util.Wind;

@Environment(EnvType.CLIENT)
public class FallingLeavesClient implements ClientModInitializer {

    /** The mod's unique identifier, used to avoid mod conflicts in the Registry and config files */
    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        Config.init();
        Leaves.init();

        // TODO: remove
        KeyBinding debug = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wind",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.wind"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (debug.wasPressed()) {
                Wind.debug();
            }
        });
    }

}
