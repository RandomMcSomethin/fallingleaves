package randommcsomethin.fallingleaves.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;
import randommcsomethin.fallingleaves.seasons.Seasons;
import randommcsomethin.fallingleaves.util.Wind;

import java.util.Map;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;
import static randommcsomethin.fallingleaves.init.Leaves.FACTORIES;

@Environment(EnvType.CLIENT)
@Mixin(value = ParticleManager.class, priority = 1010) // after Fabric API
public abstract class ParticleManagerMixin {

    @Shadow
    protected ClientWorld world;

    @Shadow @Final
    private Map<Identifier, ParticleManager.SimpleSpriteProvider> spriteAwareFactories;

    @Shadow @Final
    private Int2ObjectMap<ParticleFactory<?>> factories;

    @SuppressWarnings("unchecked")
    @Inject(method = "registerDefaultFactories", at = @At("RETURN"))
    public void registerLeafFactories(CallbackInfo ci) {
        for (var entry : Leaves.LEAVES.entrySet()) {
            var type = entry.getKey();
            var id = entry.getValue();

            var particleFactory = (ParticleFactory<BlockStateParticleEffect>) factories.get(Registries.PARTICLE_TYPE.getRawId(type));

            if (particleFactory == null) {
                var spriteProvider = SimpleSpriteProviderInvoker.init();
                spriteAwareFactories.put(id, spriteProvider);
                particleFactory = new FallingLeafParticle.BlockStateFactory(spriteProvider);
            }

            FACTORIES.put(type, particleFactory);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!CONFIG.enabled)
            return;

        Seasons.tick(world);
        Wind.tick(world);
    }

}
