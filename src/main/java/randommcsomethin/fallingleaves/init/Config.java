package randommcsomethin.fallingleaves.init;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;
import randommcsomethin.fallingleaves.config.LeafSettingsGuiProvider;

import java.lang.reflect.Field;

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
    }
}
