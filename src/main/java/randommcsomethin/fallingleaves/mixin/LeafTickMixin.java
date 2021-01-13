package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.FallingLeavesClient;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.util.TextureCache;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.*;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null)
            return;

        if (!CONFIG.dropFromPlayerPlacedBlocks && state.get(LeavesBlock.PERSISTENT))
            return;

        double spawnChance = leafSettings.getSpawnChance();

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            // Particle position
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY();
            double z = pos.getZ() + random.nextDouble();

            if (shouldSpawnParticle(world, pos, x, y, z)) {
                MinecraftClient client = MinecraftClient.getInstance();

                int color = client.getBlockColors().getColor(state, world, pos, 0);

                // no block color, try to calculate it from it's texture
                if (color == -1)
                    color = calculateBlockColor(client, state);

                double r = (color >> 16 & 255) / 255.0;
                double g = (color >> 8  & 255) / 255.0;
                double b = (color       & 255) / 255.0;

                // Add the particle.
                world.addParticle(
                    leafSettings.isConiferBlock ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF,
                    x, y, z,
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
    private static boolean shouldSpawnParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (world.getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

        double y2 = y - (CONFIG.minimumFreeSpaceBelow > 0 ? CONFIG.minimumFreeSpaceBelow : 0.2);
        Box collisionBox = new Box(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).findAny().isPresent();
    }

}
