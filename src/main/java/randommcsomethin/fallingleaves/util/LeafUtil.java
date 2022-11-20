package randommcsomethin.fallingleaves.util;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.mixin.NativeImageAccessor;
import randommcsomethin.fallingleaves.mixin.SpriteAccessor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.RegistryUtil.getBlockId;

public class LeafUtil {

    private static final Random renderRandom = Random.createLocal();

    public static double getModifiedSpawnChance(BlockState state, LeafSettingsEntry leafSettings) {
        double spawnChance = leafSettings.getSpawnChance();

        if (FabricLoader.getInstance().isModLoaded("seasons")) {
            if (FabricSeasons.getCurrentSeason() == Season.FALL) {
                // TODO this is a bit weird because some trees like Traverse's autumnal leaves already have boosted values
                // double autumn, what does it mean?
                spawnChance *= CONFIG.fallSpawnRateFactor;
            } else if (FabricSeasons.getCurrentSeason() == Season.WINTER) {
                spawnChance *= CONFIG.winterSpawnRateFactor;
            }
        }

        if (CONFIG.decaySpawnRateFactor != 1.0f) {
            if (isLeafBlock(state.getBlock(), true) && state.getBlock().hasRandomTicks(state)) { // decaying leaves have random ticks
                spawnChance *= CONFIG.decaySpawnRateFactor;
            }
        }

        return spawnChance;
    }

    public static void trySpawnLeafAndSnowParticle(BlockState state, World world, BlockPos pos, Random random) {
        if (CONFIG.startingSpawnRadius > 0) {
            assert MinecraftClient.getInstance().player != null; // guaranteed when called from randomDisplayTick

            if (LeafUtil.getMaximumDistance(MinecraftClient.getInstance().player.getBlockPos(), pos) < CONFIG.startingSpawnRadius) {
                return;
            }
        }

        // every leaf block or leaf spawner should have a settings entry
        LeafSettingsEntry leafSettings = Objects.requireNonNull(getLeafSettingsEntry(state));
        double spawnChance = LeafUtil.getModifiedSpawnChance(state, leafSettings);

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            spawnLeafParticles(1, false, state, world, pos, random, leafSettings);
        }

        // snow spawns independently from leaf particles (and the leaf block settings)
        double snowSpawnChance = CONFIG.getSnowflakeSpawnChance();
        if (snowSpawnChance != 0 && random.nextDouble() < snowSpawnChance) {
            spawnSnowParticles(1, false, state, world, pos, random, leafSettings);
        }
    }

    public static void spawnLeafParticles(int count, boolean spawnInsideBlock, BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
        if (count == 0) return;

        BlockStateParticleEffect params = new BlockStateParticleEffect(leafSettings.isConiferBlock ? Leaves.FALLING_CONIFER_LEAF : Leaves.FALLING_LEAF, state);

        spawnParticles(count, params, spawnInsideBlock, state, world, pos, random, leafSettings);
    }

    public static void spawnSnowParticles(int count, boolean spawnInsideBlock, BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
        if (count == 0) return;

        boolean snowy = false;

        // matches all snowy vanilla biomes
        if (VillagerType.forBiome(world.getBiome(pos)) == VillagerType.SNOW) {
            snowy = true;
        } else {
            // check the top for snow layers/blocks
            Block topBlock = world.getBlockState(world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).down()).getBlock();

            boolean isSnowLayer = topBlock instanceof SnowBlock; // works for seasons:seasonal_snow too
            if (isSnowLayer || topBlock == Blocks.SNOW_BLOCK || topBlock instanceof PowderSnowBlock) {
                snowy = true;
            }
        }

        // biome temperature checks for snow don't work well, Seasons globally puts them at or below 0 in winter too
        // if (world.getBiome(pos).value().getTemperature() < 0.0)
        //    snowy = true;

        if (!snowy)
            return;

        BlockStateParticleEffect params = new BlockStateParticleEffect(Leaves.FALLING_SNOW, state);

        spawnParticles(count, params, spawnInsideBlock, state, world, pos, random, leafSettings);
    }

    public static void spawnParticles(int count, BlockStateParticleEffect params, boolean spawnInsideBlock, BlockState state, World world, BlockPos pos, Random random, LeafSettingsEntry leafSettings) {
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            // Particle position
            double x = pos.getX() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double y;

            if (spawnInsideBlock) {
                y = pos.getY() + random.nextDouble();
            } else {
                y = pos.getY() - (state.isOpaque() ? 0.1 : 0); // move leaves outside of opaque blocks (to prevent them from appearing black)

                if (!hasRoomForLeafParticle(world, pos, x, y, z))
                    continue;
            }

            world.addParticle(params, x, y, z, 0, 0, 0);
        }
    }

    public static double[] getBlockTextureColor(BlockState state, World world, BlockPos pos) {
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

        return calculateLeafColor(spriteId, texture, blockColor);
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

    private static boolean hasRoomForLeafParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (isLeafBlock(world.getBlockState(pos.down()).getBlock(), true)) return false;

        double y2 = y - CONFIG.minimumFreeSpaceBelow * 0.5;
        Box collisionBox = new Box(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).iterator().hasNext();
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

    /** Block tags can only be used once the integrated server has been started */
    public static boolean isLeafBlock(Block block, boolean useBlockTags) {
        return (block instanceof LeavesBlock) || (useBlockTags && block.getDefaultState().isIn(BlockTags.LEAVES));
    }

    @Nullable
    public static LeafSettingsEntry getLeafSettingsEntry(BlockState blockState) {
        return CONFIG.leafSettings.get(getBlockId(blockState));
    }

    public static int getMaximumDistance(Vec3i v1, Vec3i v2) {
        int dx = Math.abs(v1.getX() - v2.getX());
        int dy = Math.abs(v1.getY() - v2.getY());
        int dz = Math.abs(v1.getZ() - v2.getZ());
        return Math.max(dx, Math.max(dy, dz));
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
