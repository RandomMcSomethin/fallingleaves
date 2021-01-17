package randommcsomethin.fallingleaves.config;

public class ConfigDefaults {

    public static boolean isConifer(String blockId) {
        switch (blockId) {
            case "byg:blue_spruce_leaves":
            case "byg:cypress_leaves":
            case "byg:fir_leaves":
            case "byg:orange_spruce_leaves":
            case "byg:pine_leaves":
            case "byg:red_spruce_leaves":
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

    public static double spawnRateFactor(String blockId) {
        switch (blockId) {
            // Shrubs and large leaved trees
            case "minecraft:jungle_leaves":
            case "terrestria:japenese_maple_shrub_leaves":
            case "terrestria:yucca_palm_leaves":
            case "terrestria:jungle_palm_leaves":
                return 0.0;
            // Autumn Leaves
            case "traverse:brown_autumnal_leaves":
            case "traverse:orange_autumnal_leaves":
            case "traverse:red_autumnal_leaves":
                return 1.8;
            // For fun
            case "terrestria:sakura_leaves":
                return 1.4; // Note: Version 1.4 had spawn rate 2.0
            default:
                return 1.0;
        }
    }

}
