package randommcsomethin.fallingleaves.config.gui;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class IdentifierSetGuiProvider implements GuiProvider {

    @SuppressWarnings("rawtypes")
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        return Collections.singletonList(
            ConfigEntryBuilder.create()
                .startStrList(
                    new TranslatableText(i13n),
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
