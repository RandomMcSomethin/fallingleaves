package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.util.TextureCache;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.util.LeafUtil.*;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null) return;

        double spawnChance = leafSettings.getSpawnChance();

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            if (isBottomLeafBlock(world, pos)) {
                MinecraftClient client = MinecraftClient.getInstance();

                int color = client.getBlockColors().getColor(state, world, pos, 0);

                // no block color, try to calculate it from it's texture
                if (color == -1)
                    color = calculateBlockColor(client, state);

                double r = (color >> 16 & 255) / 255.0;
                double g = (color >> 8  & 255) / 255.0;
                double b = (color       & 255) / 255.0;

                double xOffset = random.nextDouble();
                double zOffset = random.nextDouble();

                // Add the particle.
                world.addParticle(
                    leafSettings.isConiferBlock ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF,
                    pos.getX() + xOffset, pos.getY(), pos.getZ() + zOffset,
                    r, g, b
                );
            }
        }
    }

    @Unique
    private static int calculateBlockColor(MinecraftClient client, BlockState state) {
        String texture = spriteToTexture(client.getBlockRenderManager().getModel(state).getSprite());

        try {
            Resource res = client.getResourceManager().getResource(new Identifier(texture));
            String resourcePack = res.getResourcePackName();
            TextureCache.Data cache = TextureCache.INST.get(texture);

            // only use cached color when resourcePack matches
            if (cache != null && resourcePack.equals(cache.resourcePack)) {
                LOGGER.debug("{}: Assigned color {}", texture, cache.color);
                return cache.color;
            } else {
                // read and cache texture color
                try (InputStream is = res.getInputStream()) {
                    Color average = averageColor(ImageIO.read(is));
                    int color = average.getRGB();
                    LOGGER.debug("{}: Calculated color {} = {} ", texture, average, color);
                    TextureCache.INST.put(texture, new TextureCache.Data(color, resourcePack));
                    return color;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't access resource {}", texture, e);
            return -1;
        }
    }

    @Unique
    private static boolean isBottomLeafBlock(World world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState blockState = world.getBlockState(belowPos);

        // TODO make more legible, e.g. why do we allow a solid block below pos?
        return blockState.isSideSolidFullSquare(world, belowPos, Direction.UP)
            || blockState.isTranslucent(world, belowPos)
            || blockState.isSolidBlock(world, belowPos);
    }

}
