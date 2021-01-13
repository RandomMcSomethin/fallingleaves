package randommcsomethin.fallingleaves.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.block.Block;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.util.List;

public class OverrideProvider implements GuiProvider {
    public static final TranslatableText resetKey = new TranslatableText("text.cloth-config.reset_value");

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        try {
            final ObjectLinkedOpenHashSet<OverrideEntry> overrideList = (ObjectLinkedOpenHashSet<OverrideEntry>) field.get(config);
            final ReferenceArrayList<AbstractConfigListEntry> entries = ReferenceArrayList.wrap(new AbstractConfigListEntry[overrideList.size()], 0);

            final SubCategoryBuilder listBuilder = new SubCategoryBuilder(resetKey, new TranslatableText("config.fallingleaves.overrides"));

            for (final OverrideEntry leafBlock : overrideList) {
                final Block block = leafBlock.getBlock();

                if (block != null) {
                    final SubCategoryBuilder builder = new SubCategoryBuilder(resetKey, new TranslatableText(block.getTranslationKey()));

                    builder.add(0, new BooleanToggleBuilder(resetKey, new TranslatableText("config.fallingleaves.use_custom_spawn_rate"), leafBlock.useCustomSpawnRate)
                        .setDefaultValue(OverrideConfiguration.getDefaultUseCustomSpawnRate(leafBlock))
                        .setSaveConsumer((final Boolean useGlobalRate) -> {
                            leafBlock.useCustomSpawnRate = useGlobalRate;
                        })
                        .build()
                    );

                    builder.add(1, new IntSliderBuilder(resetKey, new TranslatableText("config.fallingleaves.custom_spawn_rate"), leafBlock.spawnRate, 0, 10)
                        .setDefaultValue(OverrideConfiguration.getDefaultSpawnRate(leafBlock))
                        .setSaveConsumer((final Integer spawnRate) -> {
                            leafBlock.spawnRate = spawnRate;
                        })
                        .build()
                    );

                    builder.add(2, new BooleanToggleBuilder(resetKey, new TranslatableText("config.fallingleaves.is_conifer"), leafBlock.isConiferBlock)
                        .setDefaultValue(OverrideConfiguration.getDefaultIsConifer(leafBlock))
                        .setSaveConsumer((final Boolean isConiferBlock) -> {
                            leafBlock.isConiferBlock = isConiferBlock;
                        })
                        .build()
                    );

                    listBuilder.add(builder.build());
                }
            }

            entries.add(listBuilder.build());

            return entries;
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}