package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.util.LeafUtil;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.getLeafSettingsEntry;
import static randommcsomethin.fallingleaves.util.LeafUtil.trySpawnLeafParticle;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void randomLeafBlockTick(BlockState state, World world, BlockPos pos, AbstractRandom random, CallbackInfo ci) {
        LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null)
            return;

        if (!CONFIG.dropFromPlayerPlacedBlocks && state.get(LeavesBlock.PERSISTENT))
            return;

        trySpawnLeafParticle(state, world, pos, random);
    }

    // TODO this only runs server-side and will thus only work in singleplayer
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random, CallbackInfo ci) {
        if (CONFIG.maxDecayLeaves == 0)
            return;

        MinecraftClient.getInstance().execute(() -> {
            ClientWorld clientWorld = MinecraftClient.getInstance().world;
            if (clientWorld == null)
                return;

            LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);
            if (leafSettings == null)
                return;

            // binomial distribution - extremes are less likely
            int count = 0;
            for (int i = 0; i < CONFIG.maxDecayLeaves; i++) {
                if (random.nextBoolean()) {
                    count++;
                }
            }

            LeafUtil.spawnLeafParticles(count, true, state, clientWorld, pos, random, leafSettings);
        });
    }

}
