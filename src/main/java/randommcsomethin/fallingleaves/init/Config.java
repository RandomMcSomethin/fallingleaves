package randommcsomethin.fallingleaves.init;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;
import randommcsomethin.fallingleaves.config.LeafSettingsGuiProvider;

import java.lang.reflect.Field;

import static randommcsomethin.fallingleaves.LeafUtils.LOGGER;

public class Config {

    public static FallingLeavesConfig CONFIG;

    public static void init() {
        FallingLeavesConfig.instance = AutoConfig.register(FallingLeavesConfig.class, GsonConfigSerializer::new).getConfig();

        AutoConfig.getGuiRegistry(FallingLeavesConfig.class)
            .registerPredicateProvider(
                new LeafSettingsGuiProvider(),
                (Field field) -> field.getName().equals("entries")
            );

        CONFIG = FallingLeavesConfig.instance;

        if (CONFIG.displayDebugData) {
            if (LOGGER.getLevel().compareTo(Level.DEBUG) < 0) {
                Configurator.setLevel(LOGGER.getName(), Level.DEBUG);
            }
        }
    }
}
