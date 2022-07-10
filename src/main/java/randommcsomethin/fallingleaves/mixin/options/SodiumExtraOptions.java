package randommcsomethin.fallingleaves.mixin.options;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import randommcsomethin.fallingleaves.config.FallingLeavesConfig;
import randommcsomethin.fallingleaves.init.Config;

import java.util.List;

@Mixin(SodiumExtraGameOptionPages.class)
public class SodiumExtraOptions {
	@ModifyVariable(method = "particle", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;", shift = At.Shift.BEFORE), remap = false)
	private static List<OptionGroup> addOption(List<OptionGroup> elements) {
		elements.add(OptionGroup.createBuilder()
				.add(OptionImpl.createBuilder(Boolean.TYPE, new OptionStorage<FallingLeavesConfig>() {
					@Override
					public FallingLeavesConfig getData() {
						return Config.CONFIG;
					}

					@Override
					public void save() {
						Config.save();
					}
				})
						.setName(Text.translatable("text.autoconfig.fallingleaves.title"))
						.setTooltip(Text.translatable("option.fallingleaves.particle.tooltip"))
						.setControl(TickBoxControl::new)
						.setBinding((fallingLeavesConfig, aBoolean) -> fallingLeavesConfig.enabled = aBoolean,
								fallingLeavesConfig -> fallingLeavesConfig.enabled)
						.build())
				.build());
		return elements;
	}
}
