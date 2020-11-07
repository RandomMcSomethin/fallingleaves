package fallingleaves.fallingleaves;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class FallingLeaves implements ModInitializer {

    public static FallingLeavesConfig config;

    public static Block[] coniferLeaves = new Block[] {
            Blocks.SPRUCE_LEAVES
    };

    public static Identifier id(String path) {
        return new Identifier("fallingleaves", path);
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(FallingLeavesConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(FallingLeavesConfig.class).getConfig();
        //FALLING_LEAF = register("falling_leaf", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128);
    }
}
