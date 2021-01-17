package randommcsomethin.fallingleaves.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.DefaultParticleType;
import randommcsomethin.fallingleaves.particle.FallingConiferLeafParticle;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;
import randommcsomethin.fallingleaves.util.RegistryUtil;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

public class Leaves {
    public static DefaultParticleType FALLING_LEAF;
    public static DefaultParticleType FALLING_CONIFER_LEAF;

    public static void init() {
        LOGGER.debug("Registering leaf particles.");

        FALLING_LEAF = RegistryUtil.registerNewLeafParticle("falling_leaf");
        FALLING_CONIFER_LEAF = RegistryUtil.registerNewLeafParticle("falling_leaf_conifer");

        ParticleFactoryRegistry.getInstance().register(FALLING_LEAF, FallingLeafParticle.DefaultFactory::new);
        ParticleFactoryRegistry.getInstance().register(FALLING_CONIFER_LEAF, FallingConiferLeafParticle.DefaultFactory::new);
    }
}
