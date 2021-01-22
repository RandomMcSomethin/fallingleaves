package randommcsomethin.fallingleaves.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Leaves;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafUtil {

    public static void trySpawnLeafParticle(BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
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

    private static int calculateBlockColor(MinecraftClient client, BlockState state) {
        Identifier texture = spriteToTexture(client.getBlockRenderManager().getModel(state).getSprite());

        try {
            Resource res = client.getResourceManager().getResource(texture);
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

    private static boolean shouldSpawnParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (world.getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

        double y2 = y - (CONFIG.minimumFreeSpaceBelow > 0 ? CONFIG.minimumFreeSpaceBelow : 0.2);
        Box collisionBox = new Box(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).findAny().isPresent();
    }

    public static Map<String, LeafSettingsEntry> getRegisteredLeafBlocks() {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return (block instanceof LeavesBlock);
            })
            .map(Identifier::toString)
            .collect(Collectors.toMap(
                Function.identity(),
                LeafSettingsEntry::new
            ));
    }

    @Nullable
    public static LeafSettingsEntry getLeafSettingsEntry(BlockState blockState) {
        String blockId = RegistryUtil.getBlockId(blockState);
        return CONFIG.leafSettings.get(blockId);
    }

    public static Color averageColor(BufferedImage image) {
        long r = 0;
        long g = 0;
        long b = 0;
        int n = 0;

        // TODO: This entire block feels like it could be simplified or broken down into
        //       more manageable parts.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y), true);

                // Only take completely opaque pixels into account
                if (c.getAlpha() == 255) {
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }
            }
        }

        return new Color(
            (int) (r / n),
            (int) (g / n),
            (int) (b / n)
        );
    }

    public static Identifier spriteToTexture(Sprite sprite) {
        String texture = sprite.getId().getPath(); // e.g. block/sakura_leaves
        return new Identifier(sprite.getId().getNamespace(), "textures/" + texture + ".png");
    }

}
