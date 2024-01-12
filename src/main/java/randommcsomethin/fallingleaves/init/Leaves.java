package randommcsomethin.fallingleaves.init;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.util.LeafUtil;
import randommcsomethin.fallingleaves.util.TextureCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.getLeafSettingsEntry;
import static randommcsomethin.fallingleaves.util.RegistryUtil.makeId;

public class Leaves {
    public static ParticleType<BlockStateParticleEffect> FALLING_LEAF = FabricParticleTypes.complex(true, BlockStateParticleEffect.PARAMETERS_FACTORY);
    public static ParticleType<BlockStateParticleEffect> FALLING_CONIFER_LEAF = FabricParticleTypes.complex(true, BlockStateParticleEffect.PARAMETERS_FACTORY);
    public static ParticleType<BlockStateParticleEffect> FALLING_SNOW = FabricParticleTypes.complex(true, BlockStateParticleEffect.PARAMETERS_FACTORY);

    public static final List<ParticleType<BlockStateParticleEffect>> TYPES = List.of(FALLING_LEAF, FALLING_CONIFER_LEAF, FALLING_SNOW);
    public static final Map<ParticleType<BlockStateParticleEffect>, Identifier> IDS = Map.of(
        FALLING_LEAF, makeId("falling_leaf"),
        FALLING_CONIFER_LEAF, makeId("falling_leaf_conifer"),
        FALLING_SNOW, makeId("falling_snow")
    );
    public static final Map<ParticleType<BlockStateParticleEffect>, ParticleFactory<BlockStateParticleEffect>> FACTORIES = new HashMap<>();

    private static boolean preLoadedRegisteredLeafBlocks = false;

    public static void init() {
        registerReloadListener();
        registerAttackBlockLeaves();
    }

    private static void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void reload(ResourceManager resourceManager) {
                // This is called before the block tags are usable, so we'll get an incomplete list of leaf blocks
                // Still better than having an empty settings menu on first launch
                if (!preLoadedRegisteredLeafBlocks) {
                    for (var registered : LeafUtil.getRegisteredLeafBlocks(false).entrySet())
                        CONFIG.leafSettings.computeIfAbsent(registered.getKey(), k -> registered.getValue());

                    preLoadedRegisteredLeafBlocks = true;
                }

                TextureCache.INST.clear();
            }

            @Override
            public Identifier getFabricId() {
                return makeId("resource_reload_listener");
            }
        });
    }

    /** Spawn between 0 and 3 leaves on hitting a leaf block */
    private static void registerAttackBlockLeaves() {
        AttackBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) -> {
            if (!CONFIG.enabled || !CONFIG.leavesOnBlockHit || !world.isClient)
                return ActionResult.PASS;

            BlockState state = world.getBlockState(pos);
            LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

            if (leafSettings != null) {
                if (leafSettings.spawnBreakingLeaves) {
                    // binomial distribution - extremes (0 or 3 leaves) are less likely
                    int count = 0;
                    for (int i = 0; i < 3; i++) {
                        if (world.random.nextBoolean()) {
                            count++;
                        }
                    }

                    LeafUtil.spawnLeafParticles(count, false, state, world, pos, world.random, leafSettings);
                }

                // spawn a bit of snow too
                if (CONFIG.getSnowflakeSpawnChance() != 0) {
                    int snowCount = 0;
                    for (int i = 0; i < 6; i++) {
                        if (world.random.nextBoolean()) {
                            snowCount++;
                        }
                    }

                    LeafUtil.spawnSnowParticles(snowCount, false, state, world, pos, world.random, leafSettings);
                }
            }

            return ActionResult.PASS;
        });
    }
}
