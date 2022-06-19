package randommcsomethin.fallingleaves.config;

import net.minecraft.util.Identifier;

public class ConfigDefaults {

    public static boolean isConifer(Identifier blockId) {
        switch (blockId.toString()) {
            case "bewitchment:cypress_leaves":
            case "bewitchment:dragons_blood_leaves":
            case "bewitchment:juniper_leaves":
            case "biomemakeover:swamp_cypress_leaves":
            case "byg:araucaria_leaves":
            case "byg:blue_spruce_leaves":
            case "byg:cypress_leaves":
            case "byg:fir_leaves":
            case "byg:orange_spruce_leaves":
            case "byg:pine_leaves":
            case "byg:red_spruce_leaves":
            case "byg:redwood_leaves":
            case "byg:yellow_spruce_leaves":
            case "minecraft:spruce_leaves":
            case "terrestria:cypress_leaves":
            case "terrestria:hemlock_leaves":
            case "terrestria:redwood_leaves":
            case "traverse:fir_leaves":
            case "woods_and_mires:pine_leaves":
                return true;

            default:
                return false;
        }
    }

    public static double spawnRateFactor(Identifier blockId) {
        switch (blockId.toString()) {
            // Shrubs and large leaved trees
            case "byg:palm_leaves":
            case "minecraft:jungle_leaves":
            case "promenade:palm_leaves":
            case "terrestria:japenese_maple_shrub_leaves":
            case "terrestria:jungle_palm_leaves":
            case "terrestria:yucca_palm_leaves":
                return 0.0;

            // Autumn Leaves
            case "promenade:autumn_birch_leaves":
            case "promenade:autumn_oak_leaves":
            case "traverse:brown_autumnal_leaves":
            case "traverse:orange_autumnal_leaves":
            case "traverse:red_autumnal_leaves":
            case "traverse:yellow_autumnal_leaves":
                return 1.8;

            // For fun and flavor
            case "byg:pink_cherry_leaves":
            case "byg:skyris_leaves":
            case "byg:white_cherry_leaves":
            case "promenade:pink_cherry_leaves":
            case "promenade:white_cherry_leaves":
            case "terrestria:sakura_leaves":
                return 1.4;

            default:
                return 1.0;
        }
    }

}
