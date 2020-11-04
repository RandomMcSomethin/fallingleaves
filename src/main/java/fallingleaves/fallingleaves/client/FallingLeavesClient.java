package fallingleaves.fallingleaves.client;

import fallingleaves.fallingleaves.FallingLeaves;
import fallingleaves.fallingleaves.particle.DynamicLeafParticle;
import fallingleaves.fallingleaves.particle.FallingLeafParticle;
import fallingleaves.fallingleaves.particle.FallingSpruceLeafParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class FallingLeavesClient implements ClientModInitializer {

    public static DefaultParticleType FALLING_LEAF;
    public static DefaultParticleType FALLING_SPRUCE_LEAF;
    public static BlockStateParticleEffect DYNAMIC_FALLING_LEAF;

    @Override
    public void onInitializeClient() {
        FALLING_LEAF = Registry.register(Registry.PARTICLE_TYPE, FallingLeaves.id("falling_leaf"), FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(FALLING_LEAF, FallingLeafParticle.DefaultFactory::new);

        FALLING_SPRUCE_LEAF = Registry.register(Registry.PARTICLE_TYPE, FallingLeaves.id("falling_leaf_spruce"), FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(FALLING_SPRUCE_LEAF, FallingSpruceLeafParticle.DefaultFactory::new);

        DYNAMIC_FALLING_LEAF = Registry.register(Registry.PARTICLE_TYPE, FallingLeaves.id("dynamic_falling_leaf"), FabricParticleTypes.complex(true, new BlockDustParticle.Factory());
        ParticleFactoryRegistry.getInstance().register(DYNAMIC_FALLING_LEAF, (ParticleFactory)new BlockDustParticle.Factory());
    }
}
