package randommcsomethin.fallingleaves.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.init.Leaves;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;
import randommcsomethin.fallingleaves.util.Wind;

import static randommcsomethin.fallingleaves.init.Leaves.*;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void registerLeafFactories(RunArgs args, CallbackInfo ci) {
        var spriteFactories = ((ParticleManagerAccessor) MinecraftClient.getInstance().particleManager).getSpriteAwareFactories();

        for (var type : Leaves.TYPES) {
            Identifier id = IDS.get(type);
            spriteFactories.put(id, SimpleSpriteProviderInvoker.init());
            FACTORIES.put(type, new FallingLeafParticle.BlockStateFactory(spriteFactories.get(id)));
        }
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void initWind(ClientWorld world, CallbackInfo ci) {
        Wind.init();
    }

}
