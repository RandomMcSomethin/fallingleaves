package fallingleaves.fallingleaves.mixin;

import fallingleaves.fallingleaves.FallingLeaves;
import fallingleaves.fallingleaves.client.FallingLeavesClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeafTickMixin {
    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        if (random.nextInt(75) == 0) {
            Direction direction = Direction.DOWN;
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!(!blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite()) && !blockState.isTranslucent(world, blockPos))) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double)direction.getOffsetX() * 0.6D;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double)direction.getOffsetZ() * 0.6D;

                int j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, 0);
                float k = (float)(j >> 16 & 255) / 255.0F;
                float l = (float)(j >> 8 & 255) / 255.0F;
                float m = (float)(j & 255) / 255.0F;

                //Regular leaves
                /*
                for (int leaf = 0; leaf < FallingLeaves.coniferLeaves.length; leaf++) {
                    if (state.getBlock() == FallingLeaves.coniferLeaves[leaf]) {
                        world.addParticle(FallingLeavesClient.FALLING_SPRUCE_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    } else {
                        world.addParticle(FallingLeavesClient.FALLING_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    }
                }

                 */
                //Dynamic leaves
                world.addParticle(new BlockStateParticleEffect((FallingLeavesClient.DYNAMIC_FALLING_LEAF, state), (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
            }
        }
    }
}
