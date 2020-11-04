package fallingleaves.fallingleaves;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class FallingLeaves implements ModInitializer {

    public static Block[] coniferLeaves = new Block[] {
            Blocks.SPRUCE_LEAVES
    };

    public static Identifier id(String path) {
        return new Identifier("fallingleaves", path);
    }

    @Override
    public void onInitialize() {
        //FALLING_LEAF = register("falling_leaf", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128);
    }
}
