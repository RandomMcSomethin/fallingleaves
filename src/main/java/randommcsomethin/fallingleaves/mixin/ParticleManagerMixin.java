package randommcsomethin.fallingleaves.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;
import randommcsomethin.fallingleaves.util.Wind;

import java.util.Map;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.init.Leaves.FACTORIES;
import static randommcsomethin.fallingleaves.init.Leaves.IDS;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow
    protected ClientWorld world;

    @Shadow @Final
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> spriteAwareFactories;

    @Inject(method = "registerDefaultFactories", at = @At("RETURN"))
    public void registerLeafFactories(CallbackInfo ci) {
        for (var type : Leaves.TYPES) {
            Identifier id = IDS.get(type);

            var spriteProvider = SimpleSpriteProviderInvoker.init();
            spriteAwareFactories.put(id, spriteProvider);
            FACTORIES.put(type, new FallingLeafParticle.BlockStateFactory(spriteProvider));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickWind(CallbackInfo ci) {
        if (!CONFIG.enabled)
            return;

        Wind.tick(world);
    }

}
