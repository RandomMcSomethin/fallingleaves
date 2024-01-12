package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.util.Wind;

import java.util.Map;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Accessor
    Map<Identifier, ParticleManager.SimpleSpriteProvider> getSpriteAwareFactories();
}
