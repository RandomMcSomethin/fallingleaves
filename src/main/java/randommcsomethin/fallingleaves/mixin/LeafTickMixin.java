package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.util.LeafUtil;

import java.util.Random;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.getLeafSettingsEntry;
import static randommcsomethin.fallingleaves.util.LeafUtil.trySpawnLeafParticle;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomLeafBlockTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        LeafSettingsEntry leafSettings = getLeafSettingsEntry(state);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null)
            return;

        if (!CONFIG.dropFromPlayerPlacedBlocks && state.get(LeavesBlock.PERSISTENT))
            return;

        double spawnChance = LeafUtil.getModifiedSpawnChance(leafSettings);

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            trySpawnLeafParticle(state, world, pos, random, leafSettings);
        }
    }

}
