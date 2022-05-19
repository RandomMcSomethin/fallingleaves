package randommcsomethin.fallingleaves.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.*;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void randomLeafBlockTick(BlockState state, World world, BlockPos pos, AbstractRandom random, CallbackInfo ci) {
        // not a leaf spawner?
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        if (!CONFIG.leafSpawners.contains(id))
            return;

        trySpawnLeafParticle(state, world, pos, random);
    }

}
