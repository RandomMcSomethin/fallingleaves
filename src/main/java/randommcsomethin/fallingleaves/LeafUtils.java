package randommcsomethin.fallingleaves;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import randommcsomethin.fallingleaves.config.OverrideEntry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafUtils {

    static boolean DEBUG = CONFIG.displayDebugData;

    public static void debugLog(String debugdata) {
        if (LeafUtils.DEBUG) {
            System.out.println("[fallingleaves] " + debugdata);
        }
    }

    public static double getLeafSpawnRate(BlockState state) {
        String blockId = getBlockId(state);

        OverrideEntry overrideEntry = getOverrideConfigEntry(blockId);

        if (overrideEntry.useGlobalRate) {
            return isConifer(blockId) ? CONFIG.coniferLeafSpawnRate : CONFIG.leafSpawnRate;
        } else {
            return overrideEntry.spawnRate;
        }
    }

    private static OverrideEntry getOverrideConfigEntry(String blockId) {
        return CONFIG.override.overrideEntries
            .stream()
            .filter(blocks -> Objects.equals(blocks.identifier, blockId))
            .findFirst()
            .orElse(null);
    }

    public static boolean isConifer(BlockState blockState) {
        return isConifer(getBlockId(blockState));
    }

    public static boolean isConifer(String block) {
        return getOverrideConfigEntry(block).isConiferBlock;
    }

    public static String getBlockId(BlockState state) {
        return Registry.BLOCK.getId(state.getBlock()).toString();
    }

    public static DefaultParticleType registerParticle(String id) {
        LeafUtils.debugLog("Registering particle: " + id);
        return Registry.register(Registry.PARTICLE_TYPE, makeIdent(id), FabricParticleTypes.simple());
    }

    public static Identifier makeIdent(String path) {
        return new Identifier("fallingleaves", path);
    }

    public static Color averageColor(BufferedImage image) {
        long r = 0;
        long g = 0;
        long b = 0;
        int n = 0;

        // TODO: This entire block feels like it could be simplified.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                if ((rgb >> 24 & 255) == 255) {
                    Color c = new Color(rgb);
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }

            }
        }

        LeafUtils.debugLog("Generated new average color. RGB: " + r / n + "/" + g / n + "/" + b / n);

        return new Color((float) r / n / 255, (float) g / n / 255, (float) b / n / 255);
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
