package randommcsomethin.fallingleaves.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ParticleLeavesBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.util.LeafUtil.CHERRY_LEAVES_PARTICLE_ID;
import static randommcsomethin.fallingleaves.util.LeafUtil.PALE_OAK_LEAVES_PARTICLE_ID;

@Mixin(ParticleLeavesBlock.class)
public abstract class ParticleLeavesBlockMixin {
	@Unique
	private Identifier particleId = null;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void getParticleTypeId(int chance, ParticleEffect particle, AbstractBlock.Settings settings, CallbackInfo ci) {
		particleId = Registries.PARTICLE_TYPE.getId(particle.getType());
	}

	@ModifyExpressionValue(
		method = "randomDisplayTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I")
	)
	public int disableIfRateIsZero(int original) {
		if (!CONFIG.enabled)
			return original;

		if (CONFIG.cherrySpawnRate == 0 && particleId.equals(CHERRY_LEAVES_PARTICLE_ID))
			return -1;

		if (CONFIG.paleOakSpawnRate == 0 && particleId.equals(PALE_OAK_LEAVES_PARTICLE_ID))
			return -1;

		return original;
	}

	@ModifyArg(
		method = "randomDisplayTick",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I")
	)
	public int modifySpawnRate(int bound) {
		if (!CONFIG.enabled)
			return bound;

		int newBound = bound;

		if (particleId.equals(CHERRY_LEAVES_PARTICLE_ID)) {
			newBound = (int) (bound / CONFIG.getCherrySpawnRateFactor());
		} else if (particleId.equals(PALE_OAK_LEAVES_PARTICLE_ID)) {
			newBound = (int) (bound / CONFIG.getPaleOakSpawnRateFactor());
		}

		// make sure to not return 0, in case bound has changed
		return Math.max(1, newBound);
	}
}
