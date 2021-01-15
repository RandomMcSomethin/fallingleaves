package randommcsomethin.fallingleaves.util;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import randommcsomethin.fallingleaves.FallingLeavesClient;

import static randommcsomethin.fallingleaves.util.LogUtil.LOGGER;

/**
 * This file will contain a number of simple static methods having to do with
 * Minecraft's registry. This is broken out into a separate Utility file as it
 * is something that may be required from multiple parts of the codebase.
 *
 * TODO - Explore ParticleTypes in the registry. Should we be using it to
 *        define more precisely what kind of particles we are adding?
 */
public class RegistryUtil {

    /**
     * Register a new Leaf Particle using the name we want to give it.
     *
     * @param leafParticleName The name of the leaf particle.
     * @return DefaultParticleType
     */
    public static DefaultParticleType registerNewLeafParticle(String leafParticleName) {
        LOGGER.debug("Registering particle: {}", leafParticleName);

        return Registry.register(
            Registry.PARTICLE_TYPE,
            makeNewIdentifier(leafParticleName),
            FabricParticleTypes.simple()
        );
    }

    /**
     * Create a new identifier linked to Falling Leaves.
     *
     * @param identifierName The name of the identifier to create.
     * @return Identifier
     */
    public static Identifier makeNewIdentifier(String identifierName) {
        return new Identifier(FallingLeavesClient.MOD_ID, identifierName);
    }

    /**
     * Get the block ID for a block from its state.
     *
     * @param blockState The BlockState object.
     * @return
     */
    public static String getBlockId(BlockState blockState) {
        return Registry.BLOCK.getId(blockState.getBlock()).toString();
    }

}
