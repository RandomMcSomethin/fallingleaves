package randommcsomethin.fallingleaves;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import java.awt.*;
import java.awt.image.BufferedImage;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class LeafUtils {

    public static final Logger LOGGER = LogManager.getLogger("fallingleaves");

    public static double getLeafSpawnRate(BlockState state) {
        String blockId = getBlockId(state);
        LeafSettingsEntry leafSettingsEntry = getLeafSettingsEntry(blockId);

        if (leafSettingsEntry == null) {
            LOGGER.warn("There is no config entry for {}!", blockId); // TODO: see LeafSettings.validatePostLoad()
            return 1;
        } else {
            if (leafSettingsEntry.useCustomSpawnRate) {
                return leafSettingsEntry.spawnRate;
            } else {
                return leafSettingsEntry.isConiferBlock ? CONFIG.coniferLeafSpawnRate : CONFIG.leafSpawnRate;
            }
        }
    }

    public static boolean isConifer(BlockState state) {
        String blockId = getBlockId(state);
        LeafSettingsEntry leafSettingsEntry = getLeafSettingsEntry(blockId);

        if (leafSettingsEntry == null) {
            LOGGER.warn("There is no config entry for {}!", blockId); // TODO: see LeafSettings.validatePostLoad()
            return false;
        } else {
            return leafSettingsEntry.isConiferBlock;
        }
    }

    private static LeafSettingsEntry getLeafSettingsEntry(String blockId) {
        return CONFIG.leafSettings.entries.get(new LeafSettingsEntry(new Identifier(blockId)));
    }

    public static String getBlockId(BlockState state) {
        return Registry.BLOCK.getId(state.getBlock()).toString();
    }

    public static DefaultParticleType registerParticle(String id) {
        LOGGER.debug("Registering particle: {}", id);
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

        return new Color((int)(r / n), (int)(g / n), (int)(b / n));
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
