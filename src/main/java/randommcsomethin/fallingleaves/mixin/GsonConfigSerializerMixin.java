package randommcsomethin.fallingleaves.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// fix a crash issue when Cloth Config tries to load an empty json config file
@Mixin(value = GsonConfigSerializer.class, remap = false)
public abstract class GsonConfigSerializerMixin<T extends ConfigData> {
	@Shadow(remap = false)
	public abstract T createDefault();

	@ModifyReturnValue(method = "deserialize", at = @At(value = "RETURN"), remap = false, require = 0)
	public T returnDefaultConfigIfEmpty(T original) {
		if (original == null)
			return createDefault();

		return original;
	}
}
