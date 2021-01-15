package randommcsomethin.fallingleaves.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import randommcsomethin.fallingleaves.FallingLeavesClient;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import java.awt.*;
import java.awt.image.BufferedImage;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

/**
 * This file contains simple static methods relating to the handling of
 * individual leaf blocks and the leaf particles that they create. This
 * is separated out so that we can have cleaner and more to-the-point
 * code in the Mixin and other files.
 */
public class LeafUtil {

    /**
     * Get the rate at which particles should spawn from a given leaf block.
     *
     * @param blockState The BlockState object for the leaf block in question.
     * @return double
     */
    public static double getLeafSpawnRate(BlockState blockState) {
        String blockId = RegistryUtil.getBlockId(blockState);
        LeafSettingsEntry leafSettingsEntry = ConfigUtil.getLeafSettingsConfig(blockId);

        if (leafSettingsEntry == null) {
            // TODO: see LeafSettings.validatePostLoad()
            FallingLeavesClient.LOGGER.warn("There is no config entry for " + blockId);
            // Spawn rates changed from default 1 to default 5.
            return 5;
        }

        if (leafSettingsEntry.useCustomSpawnRate) {
            return leafSettingsEntry.spawnRate;
        }

        return leafSettingsEntry.isConiferBlock ? CONFIG.coniferLeafSpawnRate : CONFIG.leafSpawnRate;
    }

    /**
     * Determine if a particular leaf block should be treated as Conifer or not.
     *
     * @param blockState The BlockState value of the block in question.
     * @return boolean
     */
    public static boolean isConifer(BlockState blockState) {
        String blockId = RegistryUtil.getBlockId(blockState);
        LeafSettingsEntry leafSettingsEntry = ConfigUtil.getLeafSettingsConfig(blockId);

        if (leafSettingsEntry == null) {
            // TODO: see LeafSettings.validatePostLoad()
            FallingLeavesClient.LOGGER.warn("There is no config entry for " + blockId);
            return false;
        }

        return leafSettingsEntry.isConiferBlock;
    }

    /**
     * Get the average color of an Image.
     *
     * @param image The BufferedImage object.
     * @return Color
     */
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
