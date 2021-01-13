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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import randommcsomethin.fallingleaves.util.ModIdentification;

import java.lang.reflect.Field;
import java.util.*;

public class LeafSettingsGuiProvider implements GuiProvider {
    public static final TranslatableText resetKey = new TranslatableText("text.cloth-config.reset_value");

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        try {
            ObjectLinkedOpenHashSet<LeafSettingsEntry> leafSettingsList = (ObjectLinkedOpenHashSet<LeafSettingsEntry>) field.get(config);
            ReferenceArrayList<AbstractConfigListEntry> entries = ReferenceArrayList.wrap(new AbstractConfigListEntry[leafSettingsList.size()], 0);

            // Insert per-leaf settings ordered by translation name
            leafSettingsList.stream().sorted(LeafSettingsEntry.TranslationComparator.INSTANCE).forEachOrdered((LeafSettingsEntry leafBlock) -> {
                Block block = leafBlock.getBlock();

                // Only insert registered blocks
                if (block != null) {
                    // TODO: I think it'd be great if modified leaf blocks would show an '*' after them. Might be hard to implement
                    SubCategoryBuilder builder = new SubCategoryBuilder(resetKey, new TranslatableText(block.getTranslationKey()))
                        .setTooltip(Text.of(ModIdentification.getModInfo(block).getName()));

                    builder.add(new BooleanToggleBuilder(resetKey, new TranslatableText("config.fallingleaves.use_custom_spawn_rate"), leafBlock.useCustomSpawnRate)
                        .setDefaultValue(ConfigDefaults.useCustomSpawnRate(leafBlock))
                        .setSaveConsumer((Boolean useGlobalRate) -> {
                            leafBlock.useCustomSpawnRate = useGlobalRate;
                        })
                        .build()
                    );

                    builder.add(new IntSliderBuilder(resetKey, new TranslatableText("config.fallingleaves.custom_spawn_rate"), leafBlock.spawnRate, 0, 10)
                        .setDefaultValue(ConfigDefaults.spawnRate(leafBlock))
                        .setSaveConsumer((Integer spawnRate) -> {
                            leafBlock.spawnRate = spawnRate;
                        })
                        .build()
                    );

                    builder.add(new BooleanToggleBuilder(resetKey, new TranslatableText("config.fallingleaves.is_conifer"), leafBlock.isConiferBlock)
                        .setDefaultValue(ConfigDefaults.isConifer(leafBlock))
                        .setSaveConsumer((Boolean isConiferBlock) -> {
                            leafBlock.isConiferBlock = isConiferBlock;
                        })
                        .build()
                    );

                    entries.add(builder.build());
                }
            });

            return entries;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}