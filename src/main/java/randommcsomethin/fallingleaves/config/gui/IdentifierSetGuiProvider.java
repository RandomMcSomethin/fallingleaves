package randommcsomethin.fallingleaves.config.gui;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class IdentifierSetGuiProvider implements GuiProvider {

    public static boolean predicate(Field field) {
        if (field.getGenericType() instanceof ParameterizedType type) {
            Class<?> rawType = (Class<?>) type.getRawType();
            Type paramType = type.getActualTypeArguments()[0];

            // will work with e.g. Set<Identifier> and HashSet<Identifier> but not with Set<? extends Identifier>
            return Set.class.isAssignableFrom(rawType) && paramType.equals(Identifier.class);
        }

        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        return Collections.singletonList(
            ConfigEntryBuilder.create()
                .startStrList(
                    Text.translatable(i13n),
                    Utils.<Set<Identifier>>getUnsafely(field, config).stream()
                        .map(Identifier::toString)
                        .collect(toList())
                )
                .setDefaultValue(() -> Utils.<Set<Identifier>>getUnsafely(field, defaults).stream()
                    .map(Identifier::toString)
                    .collect(toList())
                )
                .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue.stream()
                    .map(Identifier::new)
                    .collect(toSet()))
                )
                .build()
        );
    }
}
