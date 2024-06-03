package randommcsomethin.fallingleaves.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.SimpleSpriteProvider.class)
public interface SimpleSpriteProviderInvoker {
    @Invoker("<init>")
    static ParticleManager.SimpleSpriteProvider init() {
        throw new AssertionError();
    }
}
