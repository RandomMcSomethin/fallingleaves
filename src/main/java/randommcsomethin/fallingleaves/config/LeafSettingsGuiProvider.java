package randommcsomethin.fallingleaves.config;

import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import randommcsomethin.fallingleaves.util.ModUtil;
import randommcsomethin.fallingleaves.util.TranslationComparator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.util.RegistryUtil.getBlock;

public class LeafSettingsGuiProvider implements GuiProvider {
    private static final TranslatableText resetKey = new TranslatableText("text.cloth-config.reset_value");

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        try {
            Map<Identifier, LeafSettingsEntry> leafSettings = (Map<Identifier, LeafSettingsEntry>) field.get(config);
            List<AbstractConfigListEntry> entries = new ArrayList<>(leafSettings.size());

            // Insert per-leaf settings ordered by translation name
            leafSettings.entrySet().stream()
                .filter((e) -> getBlock(e.getKey()) != null) // Only insert registered blocks
                .sorted((e1, e2) -> TranslationComparator.INST.compare(getBlock(e1.getKey()).getTranslationKey(), getBlock(e2.getKey()).getTranslationKey()))
                .forEachOrdered((e) -> {
                    Identifier blockId = e.getKey();
                    LeafSettingsEntry leafEntry = e.getValue();
                    Block block = getBlock(blockId);

                    // TODO: I think it'd be great if modified leaf blocks would show an '*' after them.
                    //       Might be hard to implement. [Fourmisain]
                    SubCategoryBuilder builder = new SubCategoryBuilder(resetKey, new TranslatableText(block.getTranslationKey()))
                        .setTooltip(Text.of(ModUtil.getModInfo(block).getName()));

                    builder.add(buildSpawnRateFactorSlider(blockId, leafEntry));
                    builder.add(buildIsConiferLeavesToggle(blockId, leafEntry));

                    entries.add(builder.build());
                });

            return entries;
        } catch (IllegalAccessException e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    private static IntegerSliderEntry buildSpawnRateFactorSlider(Identifier blockId, LeafSettingsEntry entry) {
        // Percentage values
        int min = 0;
        int max = 1000;
        int stepSize = 10;
        int currentValue = (int)(entry.spawnRateFactor * 100.0);
        int defaultValue = (int)(ConfigDefaults.spawnRateFactor(blockId) * 100.0);

        min /= stepSize;
        max /= stepSize;
        currentValue /= stepSize;
        defaultValue /= stepSize;

        return new IntSliderBuilder(resetKey, new TranslatableText("config.fallingleaves.spawn_rate_factor"), currentValue, min, max)
            .setDefaultValue(defaultValue)
            .setSaveConsumer((Integer value) -> {
                entry.spawnRateFactor = (value * stepSize) / 100.0;
            })
            .setTextGetter((Integer value) -> {
                return Text.of((value * stepSize) + "%");
            })
            .setTooltip(new TranslatableText("config.fallingleaves.spawn_rate_factor.@Tooltip"))
            .build();
    }

    private static BooleanListEntry buildIsConiferLeavesToggle(Identifier blockId, LeafSettingsEntry entry) {
        return new BooleanToggleBuilder(resetKey, new TranslatableText("config.fallingleaves.is_conifer"), entry.isConiferBlock)
            .setDefaultValue(ConfigDefaults.isConifer(blockId))
            .setSaveConsumer((Boolean isConiferBlock) -> {
                entry.isConiferBlock = isConiferBlock;
            })
            .build();
    }

}