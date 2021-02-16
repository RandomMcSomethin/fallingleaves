package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.util.Wind;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        Wind.tick();
    }

}
