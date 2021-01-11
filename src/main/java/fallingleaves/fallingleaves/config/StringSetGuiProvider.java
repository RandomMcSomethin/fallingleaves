package fallingleaves.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.util.*;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

@SuppressWarnings("rawtypes")
public class StringSetGuiProvider implements GuiProvider {
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		ConfigEntryBuilder builder = ConfigEntryBuilder.create();
		Set<String> set = getUnsafely(field, config);

		return Collections.singletonList(
			builder.startStrList(new TranslatableText(i13n), new ArrayList<>(set))
				.setSaveConsumer(newValue -> setUnsafely(field, config, new TreeSet<>(newValue)))
				.build());
	}
}
