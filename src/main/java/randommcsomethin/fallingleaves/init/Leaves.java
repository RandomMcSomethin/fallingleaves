package randommcsomethin.fallingleaves.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.DefaultParticleType;
import randommcsomethin.fallingleaves.LeafUtils;
import randommcsomethin.fallingleaves.particle.FallingConiferLeafParticle;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;

public class Leaves {
    public static DefaultParticleType FALLING_LEAF;
    public static DefaultParticleType FALLING_CONIFER_LEAF;

    public static void init() {
        FALLING_LEAF = LeafUtils.registerParticle("falling_leaf");
        FALLING_CONIFER_LEAF = LeafUtils.registerParticle("falling_leaf_conifer");

        ParticleFactoryRegistry.getInstance().register(FALLING_LEAF, FallingLeafParticle.DefaultFactory::new);
        ParticleFactoryRegistry.getInstance().register(FALLING_CONIFER_LEAF, FallingConiferLeafParticle.DefaultFactory::new);
    }
}
