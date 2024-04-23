package randommcsomethin.fallingleaves.util;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import randommcsomethin.fallingleaves.FallingLeavesClient;

import java.util.Optional;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

public class RegistryUtil {

    public static ParticleType<BlockStateParticleEffect> registerNewLeafParticle(String leafParticleName) {
        LOGGER.debug("Registering particle: {}", leafParticleName);

        return Registry.register(
            Registries.PARTICLE_TYPE,
            makeId(leafParticleName),
            FabricParticleTypes.complex(true, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec)
        );
    }

    public static Identifier makeId(String path) {
        return new Identifier(FallingLeavesClient.MOD_ID, path);
    }

    public static Identifier getBlockId(BlockState blockState) {
        return Registries.BLOCK.getId(blockState.getBlock());
    }

    @Nullable
    public static Block getBlock(Identifier blockId) {
        Optional<Block> maybeBlock = Registries.BLOCK.getOrEmpty(blockId);
        return maybeBlock.orElse(null);
    }

}
