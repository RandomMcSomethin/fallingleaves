package fallingleaves.fallingleaves.mixin;

import fallingleaves.fallingleaves.FallingLeaves;
import fallingleaves.fallingleaves.LeafUtils;
import fallingleaves.fallingleaves.client.FallingLeavesClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeafTickMixin {
    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        double rateVar = 1.0;
        for (int leaf = 0; leaf < FallingLeaves.coniferLeaves.length; leaf++) {
            if (state.getBlock() == FallingLeaves.coniferLeaves[leaf])
                rateVar = FallingLeaves.config.coniferLeafRate;
                    else rateVar = FallingLeaves.config.leafRate;
        }
        if (rateVar != 0 && random.nextInt((int) (75*rateVar)) == 0) {
            BlockPos ogPos = pos;
            Direction direction = Direction.DOWN;
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!(!blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite()) && !blockState.isTranslucent(world, blockPos) && !blockState.isSolidBlock(world, blockPos))) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetX() * 0.6D;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetZ() * 0.6D;

                int j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos.offset(Direction.UP), 0);
                if (j == -1) {
                    try {
                        InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(
                                new Identifier(LeafUtils.spriteToTexture(MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getSprite()))
                        ).getInputStream();
                        BufferedImage img = ImageIO.read(is);
                        j = LeafUtils.averageColor(img, img.getWidth(), img.getHeight()).getRGB();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*
                if (j == -1) {
                    List<BakedQuad> quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, Direction.DOWN, random);
                    for (int i = 0; i < Direction.values().length; i++) {
                        Direction[] dirz = Direction.values();
                        quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, dirz[i], random);
                        System.out.println("Getting quad...");
                        if (!quads.isEmpty()) {
                            System.out.println("Quad found!");
                            if (quads.get(quads.size() - 1).hasColor()) {
                                System.out.println("Quad has color");
                                break;
                            }
                        }
                    }
                    if (!quads.isEmpty()) {
                        j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, quads.get(quads.size() - 1).getColorIndex());
                        System.out.println("Color is " + j);
                    }
                    j = state.getMaterial().getColor().color;
                    System.out.println("Default color is " + j);
                }
                */
                //if (j == 16777215) {
                //}
                float k = (float) (j >> 16 & 255) / 255.0F;
                float l = (float) (j >> 8 & 255) / 255.0F;
                float m = (float) (j & 255) / 255.0F;

                //Regular leaves
                for (int leaf = 0; leaf < FallingLeaves.coniferLeaves.length; leaf++) {
                    if (state.getBlock() == FallingLeaves.coniferLeaves[leaf]) {
                        world.addParticle(FallingLeavesClient.FALLING_SPRUCE_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    } else {
                        world.addParticle(FallingLeavesClient.FALLING_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);
                    }
                }
                //Dynamic leaves
                /*
                if (world.isClient) {
                    new DynamicLeafParticle((ClientWorld) world, (double) pos.getX() + d, pos.getY(), (double) pos.getZ() + f, k, l, m, state);
                }
                 */
            }
        }
    }
}
