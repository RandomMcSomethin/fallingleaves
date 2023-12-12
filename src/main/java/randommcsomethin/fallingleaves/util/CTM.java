package randommcsomethin.fallingleaves.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

/**
 * CTM generally stands for Connected Textures Mod, but in this case means OptiFine Connected Textures.
 * This class is meant to parse biome specific full block texture replacements on leaf blocks.
 * Documentation: https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/ctm.properties
 */
public class CTM {

    private static boolean ctmEnabled = (FabricLoader.getInstance().isModLoaded("optifabric") || FabricLoader.getInstance().isModLoaded("continuity"));

    public static boolean isEnabled() {
        return ctmEnabled;
    }

    /** Parses the OptiFine config to check if Connected Textures are enabled */
    public static void parseOptiFineConfig() {
        if (!FabricLoader.getInstance().isModLoaded("optifabric"))
            return;

        Path optiFineConfig = FabricLoader.getInstance().getGameDir().resolve("optionsof.txt");
        if (Files.exists(optiFineConfig)) {
            try (BufferedReader br = Files.newBufferedReader(optiFineConfig)) {
                br.lines().forEach((line) -> {
                    String[] option = line.split(":", 2);

                    if (option.length != 2)
                        return;

                    // 1 = fast, 2 = fancy, 3 = off
                    if (option[0].equalsIgnoreCase("ofConnectedTextures") && option[1].equals("3")) {
                        ctmEnabled = false;
                    } else {
                        LOGGER.info("OptiFine Connected Textures are enabled.");
                    }
                });
            } catch (IOException e) {
                LOGGER.error("Failed to read OptiFine Connected Texture setting", e);
            }
        }
    }

    public static void readBiomeTextures(ResourceManager resManager) {
        long t1 = System.nanoTime();

        // TODO: test if this works with mod textures (it should)

        TextureCache.biomeTextures.clear(); // this will do

        // TODO: ideally this should be filtered only for leaf blocks, but at the time of resource loading block tags aren't yet available
        Map<Identifier, Resource> propertiesIds = resManager.findResources(
            "optifine/ctm",
            id -> id.getPath().endsWith(".properties")
        );

        // we assume there is only one .properties for each full block replacement, so we don't do any sorting
        for (var entry : propertiesIds.entrySet()) {
            Identifier location = entry.getKey();
            Resource resource = entry.getValue();

            String propertiesPath = location.getPath();
            LOGGER.debug("propertiesPath = {}", propertiesPath);

            int lastPathSeparator = propertiesPath.lastIndexOf('/');
            String basePath = propertiesPath.substring(0, lastPathSeparator + 1);
            String baseFilename = propertiesPath.substring(lastPathSeparator + 1, propertiesPath.length() - ".properties".length());

            try (InputStream is = resource.getInputStream()) {
                Properties prop = new Properties();
                prop.load(is);

                String tiles       = prop.getProperty("tiles");
                String matchTiles  = prop.getProperty("matchTiles");
                String matchBlocks = prop.getProperty("matchBlocks");
                String biomes      = prop.getProperty("biomes");

                LOGGER.debug("tiles = {}", tiles);
                LOGGER.debug("matchTiles = {}", matchTiles);
                LOGGER.debug("matchBlocks = {}", matchBlocks);
                LOGGER.debug("biomes = {}", biomes);

                // tiles are required and we are only interested in biome specific textures
                if (tiles == null || biomes == null)
                    continue;

                // fallback to matching the filename
                if (matchTiles == null && matchBlocks == null)
                    matchTiles = baseFilename;

                String texturePath = parseTiles(basePath, tiles);
                if (texturePath == null)
                    continue;

                Identifier textureId = new Identifier(location.getNamespace(), texturePath);

                // sanity check: does the texture exist?
                if (resManager.getResource(textureId).isEmpty()) {
                    LOGGER.warn("OptiFine CTM texture doesn't exist. textureId = {}", textureId);
                    continue;
                }

                for (Identifier biomeId : parseBiome(biomes)) {
                    for (Identifier blockId : parseMatches(matchTiles, matchBlocks)) {
                        LOGGER.debug("put (biome: {}, blockId: {}) -> {}", biomeId, blockId, textureId);
                        TextureCache.biomeTextures.put(new TextureCache.BiomeBlock(biomeId, blockId), textureId);
                    }
                }
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("Couldn't read OptiFine CTM properties file");
            }
        }

        long t2 = System.nanoTime();
        LOGGER.debug("readBiomeTextures() took {} ms", (t2 - t1) / 1000000L);
    }

    private static List<Identifier> parseBiome(String biomes) {
        List<Identifier> biomeList = new ArrayList<>();

        for (String biome : biomes.split(" ")) {
            if (biome.isEmpty()) continue;

            // convert e.g. DarkForest -> dark_forest
            StringBuilder biomeId = new StringBuilder();
            for (int i = 0; i < biome.length(); i++) {
                char c = biome.charAt(i);

                if (Character.isUpperCase(c)) {
                    if (i > 0) biomeId.append('_');
                    c = Character.toLowerCase(c);
                }

                biomeId.append(c);
            }

            try {
                biomeList.add(new Identifier(biomeId.toString()));
            } catch (Exception e) {
                LOGGER.error("couldn't parse biome \"{}\"", biome);
            }
        }

        return biomeList;
    }

    private static List<Identifier> parseMatches(String matchTile, String matchBlocks) {
        List<Identifier> matches = new ArrayList<>();

        /* TODO:
         *  What does it mean to "match a tile"?
         *  How do matchTile and matchBlocks interact; do they match the union or the intersection?
         *  What does this excerpt from the documentation mean:
         *  # Tiles output by CTM rules can also be matched by another rule.
         *  # The tile name is simply the full path to the tile
         *  #   matchTiles=optifine/ctm/mygrass/1.png
         */

        if (matchTile != null) {
            // currently interpreting tiles as block ids
            for (String tile : matchTile.split(" "))
                matches.add(new Identifier(tile));
        }

        if (matchBlocks != null) {
            for (String block : matchBlocks.split(" ")) {
                int colonCount = 0;
                for (int i = 0; i < block.length(); i++) {
                    if (block.charAt(i) == ':') {
                        colonCount++;
                    }
                }

                // skip block ids with properties
                if (colonCount > 1)
                    continue;

                // currently forming the union
                matches.add(new Identifier(block));
            }
        }

        return matches;
    }

    /** Returns full block replacement texture of CTM's properties' "tiles" field */
    @Nullable
    private static String parseTiles(String basePath, String tiles) {
        // skip if tiles is a list, e.g. "0-4 5 some/other/name.png"
        if (tiles.indexOf(' ') != -1)
            return null;

        // skip if "<skip>" or "<default>"
        if (tiles.charAt(0) == '<')
            return null;

        // skip single tile replacement, e.g. "0"
        try {
            Integer.parseInt(tiles);
            return null;
        } catch (NumberFormatException ignored) { }

        // skip tile replacements, e.g. "0-2"
        String[] s = tiles.split("-");
        if (s.length == 2) {
            try {
                Integer.parseInt(s[0]);
                Integer.parseInt(s[1]);
                return null;
            } catch (NumberFormatException ignored) { }
        }

        // return absolute path as-is
        if (tiles.indexOf('/') != -1)
            return tiles;

        if (!tiles.endsWith(".png"))
            tiles = tiles + ".png";

        return basePath + tiles;
    }

}
