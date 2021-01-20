package randommcsomethin.fallingleaves.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafUtil {

    public static Map<String, LeafSettingsEntry> getRegisteredLeafBlocks() {
        return Registry.BLOCK
            .getIds()
            .stream()
            .filter(entry -> {
                Block block = Registry.BLOCK.get(entry);
                return (block instanceof LeavesBlock);
            })
            .map(Identifier::toString)
            .collect(Collectors.toMap(
                Function.identity(),
                LeafSettingsEntry::new
            ));
    }

    @Nullable
    public static LeafSettingsEntry getLeafSettingsEntry(BlockState blockState) {
        String blockId = RegistryUtil.getBlockId(blockState);
        return CONFIG.leafSettings.get(blockId);
    }

    public static Color averageColor(BufferedImage image) {
        long r = 0;
        long g = 0;
        long b = 0;
        int n = 0;

        // TODO: This entire block feels like it could be simplified or broken down into
        //       more manageable parts.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y), true);

                // Only take completely opaque pixels into account
                if (c.getAlpha() == 255) {
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }
            }
        }

        return new Color(
            (int) (r / n),
            (int) (g / n),
            (int) (b / n)
        );
    }

    public static String spriteToTexture(Sprite sprite) {
        String id = sprite.getId().toString(); // e.g. terrestria:block/sakura_leaves

        int s = id.indexOf(':');
        assert s != -1;

        String modId = id.substring(0, s);
        String texture = id.substring(s + 1);

        return modId + ":textures/" + texture + ".png";
    }

}
