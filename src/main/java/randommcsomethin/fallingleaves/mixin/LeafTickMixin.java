package randommcsomethin.fallingleaves.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.FallingLeavesClient;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import java.util.Random;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
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

        double spawnChance = leafSettings.getSpawnChance();

        if (FabricLoader.getInstance().isModLoaded("seasons")) {
            if (FabricSeasons.getCurrentSeason() == Season.FALL) {
                // TODO autumnal leaves probably shouldn't be boosted twice
                spawnChance *= 1.8;
                LOGGER.debug("boost");
            } else if (FabricSeasons.getCurrentSeason() == Season.WINTER) {
                spawnChance *= 0.1;
                LOGGER.debug("slow");
            }
        }

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            trySpawnLeafParticle(state, world, pos, random, leafSettings);
        }
    }

}
