package randommcsomethin.fallingleaves.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.RegistryUtil.getBlockId;

public class LeafUtil {

    public static void trySpawnLeafParticle(BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
        // Particle position
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY();
        double z = pos.getZ() + random.nextDouble();

        if (shouldSpawnParticle(world, pos, x, y, z)) {
            MinecraftClient client = MinecraftClient.getInstance();

            // read the bottom quad to determine whether we should color the texture
            BakedModel model = client.getBlockRenderManager().getModel(state);
            List<BakedQuad> quads = model.getQuads(state, Direction.DOWN, random);
            boolean shouldColor = quads.isEmpty() || quads.get(0).hasColor();

            int blockColor = client.getBlockColors().getColor(state, world, pos, 0);
            Identifier texture = spriteToTexture(model.getSprite());

            double[] color = calculateLeafColor(texture, shouldColor, blockColor, client);

            double r = color[0];
            double g = color[1];
            double b = color[2];

            // Add the particle.
            world.addParticle(
                leafSettings.isConiferBlock ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF,
                x, y, z,
                r, g, b
            );
        }
    }

    private static double[] calculateLeafColor(Identifier texture, boolean shouldColor, int blockColor, MinecraftClient client) {
        try {
            Resource res = client.getResourceManager().getResource(texture);
            String resourcePack = res.getResourcePackName();
            TextureCache.Data cache = TextureCache.INST.get(texture);
            double[] textureColor;

            // only use cached texture color when resourcePack matches
            if (cache != null && resourcePack.equals(cache.resourcePack)) {
                textureColor = cache.getColor();
            } else {
                // read and cache texture color
                try (InputStream is = res.getInputStream()) {
                    textureColor = averageColor(ImageIO.read(is));
                    TextureCache.INST.put(texture, new TextureCache.Data(textureColor, resourcePack));
                    LOGGER.debug("{}: Calculated texture color {} ", texture, textureColor);
                }
            }

            if (shouldColor && blockColor != -1) {
                // multiply texture and block color RGB values (in range 0-1)
                textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
                textureColor[1] *= (blockColor >> 8  & 255) / 255.0;
                textureColor[2] *= (blockColor       & 255) / 255.0;
            }

            return textureColor;
        } catch (IOException e) {
            LOGGER.error("Couldn't access resource {}", texture, e);
            return new double[] { 1, 1, 1 };
        }
    }

    private static boolean shouldSpawnParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (world.getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

        double y2 = y - CONFIG.minimumFreeSpaceBelow * 0.5;
        Box collisionBox = new Box(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).findAny().isPresent();
    }

    public static Map<Identifier, LeafSettingsEntry> getRegisteredLeafBlocks() {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return (block instanceof LeavesBlock);
            })
            .collect(Collectors.toMap(
                Function.identity(),
                LeafSettingsEntry::new
            ));
    }

    @Nullable
    public static LeafSettingsEntry getLeafSettingsEntry(BlockState blockState) {
        return CONFIG.leafSettings.get(getBlockId(blockState));
    }

    public static double[] averageColor(BufferedImage image) {
        double r = 0;
        double g = 0;
        double b = 0;
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

        return new double[] {
            (r / n) / 255.0,
            (g / n) / 255.0,
            (b / n) / 255.0
        };
    }

    public static Identifier spriteToTexture(Sprite sprite) {
        String texture = sprite.getId().getPath(); // e.g. block/sakura_leaves
        return new Identifier(sprite.getId().getNamespace(), "textures/" + texture + ".png");
    }

}
