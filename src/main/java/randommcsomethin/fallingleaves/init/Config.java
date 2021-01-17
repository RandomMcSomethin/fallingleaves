package randommcsomethin.fallingleaves.init;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;
import randommcsomethin.fallingleaves.config.LeafSettingsGuiProvider;

import java.lang.reflect.Field;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

public class Config {

    public static FallingLeavesConfig CONFIG;

    private static ConfigHolder<FallingLeavesConfig> configHolder;

    public static void init() {
        configHolder = AutoConfig.register(FallingLeavesConfig.class, GsonConfigSerializer::new);
        CONFIG = configHolder.getConfig();

        if (CONFIG.displayDebugData && LOGGER.getLevel().compareTo(Level.DEBUG) < 0)
            Configurator.setLevel(LOGGER.getName(), Level.DEBUG);

        AutoConfig.getGuiRegistry(FallingLeavesConfig.class)
            .registerPredicateProvider(
                new LeafSettingsGuiProvider(),
                (Field field) -> field.getName().equals("entries")
            );

        LOGGER.debug("Loaded configuration.");
    }

    public static void save() {
        configHolder.save();
    }

}
