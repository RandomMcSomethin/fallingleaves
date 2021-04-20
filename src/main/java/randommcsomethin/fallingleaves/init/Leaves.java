package randommcsomethin.fallingleaves.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.particle.FallingConiferLeafParticle;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;
import randommcsomethin.fallingleaves.util.LeafUtil;
import randommcsomethin.fallingleaves.util.RegistryUtil;

import java.util.Map;
import java.util.Random;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.getLeafSettingsEntry;
import static randommcsomethin.fallingleaves.util.RegistryUtil.makeId;

public class Leaves {
    public static DefaultParticleType FALLING_LEAF;
    public static DefaultParticleType FALLING_CONIFER_LEAF;

    private static boolean preLoadedRegisteredLeafBlocks = false;

    public static void init() {
        LOGGER.debug("Registering leaf particles.");

        FALLING_LEAF = RegistryUtil.registerNewLeafParticle("falling_leaf");
        FALLING_CONIFER_LEAF = RegistryUtil.registerNewLeafParticle("falling_leaf_conifer");

        ParticleFactoryRegistry.getInstance().register(FALLING_LEAF, FallingLeafParticle.DefaultFactory::new);
        ParticleFactoryRegistry.getInstance().register(FALLING_CONIFER_LEAF, FallingConiferLeafParticle.DefaultFactory::new);

        registerReloadListener();
        registerAttackBlockLeaves();
    }

    private static void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager resourceManager) {
                // This is called before the block tags are usable, so we'll get an incomplete list of leaf blocks
                // Still better than having an empty settings menu on first launch
                if (!preLoadedRegisteredLeafBlocks) {
                    for (Map.Entry<Identifier, LeafSettingsEntry> registered : LeafUtil.getRegisteredLeafBlocks(false).entrySet())
                        CONFIG.leafSettings.computeIfAbsent(registered.getKey(), k -> registered.getValue());

                    preLoadedRegisteredLeafBlocks = true;
                }
            }

            @Override
            public Identifier getFabricId() {
                return makeId("resource_reload_listener");
            }
        });
    }

    /** Spawn between 0 and 3 leaves on hitting a leaf block */
    private static void registerAttackBlockLeaves() {
        Random random = new Random();

        AttackBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) -> {
            BlockState state = world.getBlockState(pos);
            LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

            if (leafSettings != null) {
                // binomial distribution - extremes (0 or 3 leaves) are less likely
                for (int i = 0; i < 3; i++) {
                    if (random.nextBoolean()) {
                        LeafUtil.trySpawnLeafParticle(state, world, pos, random, leafSettings);
                    }
                }
            }

            return ActionResult.PASS;
        });
    }
}
