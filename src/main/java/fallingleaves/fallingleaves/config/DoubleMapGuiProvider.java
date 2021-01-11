package fallingleaves.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DoubleMapGuiProvider implements GuiProvider {
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		ConfigEntryBuilder builder = ConfigEntryBuilder.create();
		List<AbstractConfigListEntry> list = new ArrayList<>();

		// add a double field for every map entry
		Map map = getUnsafely(field, config);
		for (Object key : map.keySet()) {
			Object value = map.get(key);

			double numeric;
			if (value instanceof Integer)
				numeric = (Integer)value;
			else if (value instanceof Double)
				numeric = (Double)value;
			else
				throw new IllegalArgumentException("config value " + key + " is non-numeric");

			list.add(builder.startDoubleField(new LiteralText(key.toString()), numeric)
				.setSaveConsumer(newValue -> map.put(key, newValue))
				.build());
		}

		return list;
	}
}
