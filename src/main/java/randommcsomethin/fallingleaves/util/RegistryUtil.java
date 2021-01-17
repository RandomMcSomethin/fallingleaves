package randommcsomethin.fallingleaves.util;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import randommcsomethin.fallingleaves.FallingLeavesClient;

import java.util.Optional;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

/**
 * TODO - Explore ParticleTypes in the registry. Should we be using it to
 *        define more precisely what kind of particles we are adding?
 */
public class RegistryUtil {

    public static DefaultParticleType registerNewLeafParticle(String leafParticleName) {
        LOGGER.debug("Registering particle: {}", leafParticleName);

        return Registry.register(
            Registry.PARTICLE_TYPE,
            makeNewIdentifier(leafParticleName),
            FabricParticleTypes.simple()
        );
    }

    public static Identifier makeNewIdentifier(String identifierName) {
        return new Identifier(FallingLeavesClient.MOD_ID, identifierName);
    }

    public static String getBlockId(BlockState blockState) {
        return Registry.BLOCK.getId(blockState.getBlock()).toString();
    }

    @Nullable
    public static Block getBlock(String blockId) {
        Optional<Block> maybeBlock = Registry.BLOCK.getOrEmpty(new Identifier(blockId));
        return maybeBlock.orElse(null);
    }

}
