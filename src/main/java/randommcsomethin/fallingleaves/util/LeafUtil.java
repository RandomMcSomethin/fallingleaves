package randommcsomethin.fallingleaves.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.mixin.NativeImageAccessor;
import randommcsomethin.fallingleaves.mixin.SpriteAccessor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.RegistryUtil.getBlockId;

public class LeafUtil {

    private static final Random renderRandom = new Random();

    public static void trySpawnLeafParticle(BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
        // Particle position
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY();
        double z = pos.getZ() + random.nextDouble();

        if (shouldSpawnParticle(world, pos, x, y, z)) {
            MinecraftClient client = MinecraftClient.getInstance();
            BakedModel model = client.getBlockRenderManager().getModel(state);

            renderRandom.setSeed(state.getRenderingSeed(pos));
            List<BakedQuad> quads = model.getQuads(state, Direction.DOWN, renderRandom);

            Sprite sprite;
            boolean shouldColor;

            // read data from the first bottom quad if possible
            if (!quads.isEmpty()) {
                boolean useFirstQuad = true;

                Identifier id = Registry.BLOCK.getId(state.getBlock());
                if (id.getNamespace().equals("byg")) {
                    /*
                     * some BYG leaves have their actual tinted leaf texture in an "overlay" that comes second, full list:
                     * flowering_orchard_leaves, joshua_leaves, mahogany_leaves, maple_leaves, orchard_leaves,
                     * rainbow_eucalyptus_leaves, ripe_joshua_leaves, ripe_orchard_leaves, willow_leaves
                     */
                    useFirstQuad = false;
                }

                BakedQuad quad = quads.get(useFirstQuad ? 0 : quads.size() - 1);
                sprite = quad.getSprite();
                shouldColor = quad.hasColor();
            } else {
                // fall back to block breaking particle
                sprite = model.getParticleSprite();
                shouldColor = true;
            }

            Identifier spriteId = sprite.getId();
            NativeImage texture = ((SpriteAccessor) sprite).getImages()[0]; // directly extract texture
            int blockColor = (shouldColor ? client.getBlockColors().getColor(state, world, pos, 0) : -1);

            double[] color = calculateLeafColor(spriteId, texture, blockColor);

            double r = color[0];
            double g = color[1];
            double b = color[2];

            world.addParticle(
                leafSettings.isConiferBlock ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF,
                x, y, z,
                r, g, b
            );
        }
    }

    private static double[] calculateLeafColor(Identifier spriteId, NativeImage texture, int blockColor) {
        TextureCache.Data cache = TextureCache.INST.get(spriteId);
        double[] textureColor;

        if (cache != null) {
            textureColor = cache.getColor();
        } else {
            // calculate and cache texture color
            textureColor = averageColor(texture);
            TextureCache.INST.put(spriteId, new TextureCache.Data(textureColor));
            LOGGER.debug("{}: Calculated texture color {} ", spriteId, textureColor);
        }

        if (blockColor != -1) {
            // multiply texture and block color RGB values (in range 0-1)
            textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
            textureColor[1] *= (blockColor >> 8  & 255) / 255.0;
            textureColor[2] *= (blockColor       & 255) / 255.0;
        }

        return textureColor;
    }

    private static boolean shouldSpawnParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (isLeafBlock(world.getBlockState(pos.down()).getBlock(), true)) return false;

        double y2 = y - CONFIG.minimumFreeSpaceBelow * 0.5;
        Box collisionBox = new Box(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return world.getBlockCollisions(null, collisionBox).findAny().isEmpty();
    }

    public static Map<Identifier, LeafSettingsEntry> getRegisteredLeafBlocks(boolean useBlockTags) {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> isLeafBlock(Registry.BLOCK.get(entry), useBlockTags))
            .collect(Collectors.toMap(
                Function.identity(),
                LeafSettingsEntry::new
            ));
    }

    /** Block tags can only be used once the integrated server is started */
    public static boolean isLeafBlock(Block block, boolean useBlockTags) {
        return (block instanceof LeavesBlock) || (useBlockTags && block.getDefaultState().isIn(BlockTags.LEAVES));
    }

    @Nullable
    public static LeafSettingsEntry getLeafSettingsEntry(BlockState blockState) {
        return CONFIG.leafSettings.get(getBlockId(blockState));
    }

    public static double[] averageColor(NativeImage image) {
        if (image.getFormat() != NativeImage.Format.RGBA) {
            LOGGER.error("RGBA image required, was {}", image.getFormat());
            return new double[] {1, 1, 1};
        }

        NativeImageAccessor imageAcc = (NativeImageAccessor) (Object) image;
        long pointer = imageAcc.getPointer();

        if (pointer == 0) {
            LOGGER.error("image is not allocated");
            return new double[] {1, 1, 1};
        }

        double r = 0;
        double g = 0;
        double b = 0;
        int n = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        // add up all opaque color values (this variant is much faster than using image.getPixelColor(x, y))
        for (int i = 0; i < width * height; i++) {
            int c = MemoryUtil.memGetInt(pointer + 4L * i);

            // RGBA format
            int cr = (c       & 255);
            int cg = (c >> 8  & 255);
            int cb = (c >> 16 & 255);
            int ca = (c >> 24 & 255);

            if (ca != 0) {
                r += cr;
                g += cg;
                b += cb;
                n++;
            }
        }

        return new double[] {
            (r / n) / 255.0,
            (g / n) / 255.0,
            (b / n) / 255.0
        };
    }

}
