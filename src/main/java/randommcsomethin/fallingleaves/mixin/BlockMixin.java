package randommcsomethin.fallingleaves.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.util.LeafUtil;

import java.util.Objects;
import java.util.Random;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.*;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomLeafBlockTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        // not a leaf spawner?
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        if (!CONFIG.leafSpawners.contains(id))
            return;

        trySpawnLeafParticle(state, world, pos, random);
    }

}
