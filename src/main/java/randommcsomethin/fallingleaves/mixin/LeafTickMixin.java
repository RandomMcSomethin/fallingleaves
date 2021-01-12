package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.LeafUtils;
import randommcsomethin.fallingleaves.init.Leaves;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Unique
    private static final HashMap<String, Integer> textureColor = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {

        // All rate calculation, including overrides etc, moved into LeafUtils.
        double rate = LeafUtils.getFallRate(state);

        if (rate != 0 && random.nextDouble() < 1.0 / (75 / rate)) {
            Direction direction = Direction.DOWN;
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);

            if (passesCriteria(world, direction, blockPos, blockState)) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetX() * 0.6D;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetZ() * 0.6D;

                MinecraftClient client = MinecraftClient.getInstance();

                int j = client.getBlockColors().getColor(state, world, blockPos.offset(Direction.UP), 0);

                if (j == -1) {
                    String texture = LeafUtils.spriteToTexture(client.getBlockRenderManager().getModel(state).getSprite());
                    Integer color = textureColor.get(texture);

                    if (color != null) {
                        LeafUtils.debugLog("Assigned color: " + color);
                        j = color;
                    } else {
                        try {
                            InputStream inputStream = client.getResourceManager()
                                .getResource(new Identifier(texture))
                                .getInputStream();

                            BufferedImage image = ImageIO.read(inputStream);

                            textureColor.put(texture, LeafUtils.averageColor(
                                image,
                                image.getWidth(),
                                image.getHeight()).getRGB()
                            );
                        } catch (IOException e) {
                            LeafUtils.debugLog("Problem calculating average color: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                float k = (float) (j >> 16 & 255) / 255.0F;
                float l = (float) (j >> 8 & 255) / 255.0F;
                float m = (float) (j & 255) / 255.0F;

                // Add the particle.
                world.addParticle(
                    LeafUtils.isConifer(state) ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF,
                    (double) pos.getX() + d,
                    pos.getY(),
                    (double) pos.getZ() + f, k, l, m
                );
            }
        }
    }

    /**
     * Moved the giant if statement to a method of its own for better oversight.
     * This needs to be cleaned up and made FAR more legible, however!
     */

    private boolean passesCriteria(World world, Direction direction, BlockPos blockPos, BlockState blockState) {
        return !(!blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())
            && !blockState.isTranslucent(world, blockPos) && !blockState.isSolidBlock(world, blockPos));
    }

}
