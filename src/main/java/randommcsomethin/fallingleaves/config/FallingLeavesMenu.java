package randommcsomethin.fallingleaves.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FallingLeavesMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Optional<Supplier<Screen>> optionalScreen = getConfigScreen(parent);

            if (optionalScreen.isPresent()) {
                return optionalScreen.get().get();
            }

            return parent;
        };
    }

    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(AutoConfig.getConfigScreen(FallingLeavesConfig.class, screen));
    }

}