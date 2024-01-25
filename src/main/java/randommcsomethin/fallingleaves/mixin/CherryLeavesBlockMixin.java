package randommcsomethin.fallingleaves.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.CherryLeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

@Mixin(CherryLeavesBlock.class)
public abstract class CherryLeavesBlockMixin {
	@ModifyExpressionValue(
		method = "randomDisplayTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I")
	)
	public int disableIfRateIsZero(int original) {
		if (CONFIG.cherrySpawnRate == 0)
			return -1;
		return original;
	}

	@ModifyArg(
		method = "randomDisplayTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I")
	)
	public int modifySpawnRate(int bound) {
		int newBound = (int) (bound / CONFIG.getCherrySpawnRateFactor());
		// make sure to not return 0, in case bound has changed
		return Math.max(1, newBound);
	}
}
